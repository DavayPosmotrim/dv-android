package com.davay.android.feature.waitsession.presentation

import androidx.lifecycle.viewModelScope
import com.davay.android.R
import com.davay.android.base.BaseViewModel
import com.davay.android.core.domain.impl.CommonWebsocketInteractor
import com.davay.android.core.domain.impl.GetUserIdUseCase
import com.davay.android.core.domain.models.SessionStatus
import com.davay.android.core.presentation.states.SessionState
import com.davay.android.feature.waitsession.domain.api.WaitSessionOnBoardingInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class WaitSessionViewModel @Inject constructor(
    private val waitSessionOnBoardingInteractor: WaitSessionOnBoardingInteractor,
    private val commonWebsocketInteractor: CommonWebsocketInteractor,
    private val getUserIdUseCase: GetUserIdUseCase
) : BaseViewModel() {
    private val _state = MutableStateFlow<WaitSessionState>(WaitSessionState.Content(emptyList()))
    val state = _state.asStateFlow()

    private val _sessionState =
        MutableStateFlow<SessionState?>(SessionState.Status(SessionStatus.WAITING))

    private val userId: String by lazy {
        getUserIdUseCase()
    }

    init {
        subscribeToWebsockets()
        handleSessionStatus()
    }

    fun isFirstTimeLaunch(): Boolean {
        return waitSessionOnBoardingInteractor.isFirstTimeLaunch()
    }

    fun markFirstTimeLaunch() {
        waitSessionOnBoardingInteractor.markFirstTimeLaunch()
    }

    /**
     * Метод необходим для обхода ошибки при возврате назад на экран создания сессии после
     * смены конфигурации устройства.
     */
    fun navigateToCreateSessionAndUnsubscribeWebSockets() {
        viewModelScope.launch {
            commonWebsocketInteractor.unsubscribeAll()
        }
        clearBackStackToMain()
        navigate(R.id.action_mainFragment_to_createSessionFragment)
    }

    fun navigateToNextScreen() {
        navigate(R.id.action_waitSessionFragment_to_selectMovieFragment)
    }

    private fun subscribeToWebsockets() {
        subscribeToSessionStatus()
        subscribeToUserList()
    }

    private fun subscribeToSessionStatus() {
        runSafelyUseCase(
            useCaseFlow = commonWebsocketInteractor.getSessionStatus(),
            onFailure = { error ->
                _sessionState.update { SessionState.Error(mapErrorToUiState(error)) }
            },
            onSuccess = { sessionStatus ->
                _sessionState.update {
                    SessionState.Status(sessionStatus)
                }
            }
        )
    }

    private fun subscribeToUserList() {
        runSafelyUseCase(
            useCaseFlow = commonWebsocketInteractor.getUsers(),
            onFailure = { error ->
                _state.update { WaitSessionState.Error(mapErrorToUiState(error)) }
            },
            onSuccess = { users ->
                val userListString = users
                    .sortedByDescending { it.userId == userId }
                    .map { it.name }
                _state.update {
                    WaitSessionState.Content(userListString)
                }
            }
        )
    }

    /**
     * Делаем подписку на статус сессии.
     * Этот статус не тянеттся во фрагмент, но влияет на состояние сессии
     */
    private fun handleSessionStatus() {
        viewModelScope.launch {
            _sessionState.collect { status ->
                when (status) {
                    SessionState.Status(SessionStatus.VOTING) -> navigateToNextScreen()
                    SessionState.Status(SessionStatus.CLOSED) -> navigateToCreateSessionAndUnsubscribeWebSockets()
                    is SessionState.Error -> _state.update { WaitSessionState.Error(status.error) }
                    else -> _sessionState.update { SessionState.Status(SessionStatus.WAITING) }
                }
            }
        }
    }
}