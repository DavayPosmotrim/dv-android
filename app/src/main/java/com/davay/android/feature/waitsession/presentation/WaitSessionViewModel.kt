package com.davay.android.feature.waitsession.presentation

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.davay.android.BuildConfig
import com.davay.android.R
import com.davay.android.base.BaseViewModel
import com.davay.android.core.domain.impl.CommonWebsocketInteractor
import com.davay.android.core.domain.impl.GetSessionDataUseCase
import com.davay.android.core.domain.impl.GetUserIdUseCase
import com.davay.android.core.domain.impl.LeaveSessionUseCase
import com.davay.android.core.domain.models.ErrorScreenState
import com.davay.android.core.domain.models.SessionStatus
import com.davay.android.core.presentation.states.SessionState
import com.davay.android.feature.waitsession.domain.api.WaitSessionOnBoardingInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class WaitSessionViewModel @Inject constructor(
    private val waitSessionOnBoardingInteractor: WaitSessionOnBoardingInteractor,
    private val commonWebsocketInteractor: CommonWebsocketInteractor,
    private val getUserIdUseCase: GetUserIdUseCase,
    private val leaveSessionUseCase: LeaveSessionUseCase,
    private val getSessionUseCase: GetSessionDataUseCase
) : BaseViewModel() {
    private val _state = MutableStateFlow<WaitSessionState>(WaitSessionState.Content(emptyList()))
    val state
        get() = _state.asStateFlow()

    private val _sessionState =
        MutableStateFlow<SessionState?>(SessionState.Status(SessionStatus.WAITING))

    private val sessionId by lazy {
        commonWebsocketInteractor.getSessionId()
    }

    private val userId: String by lazy {
        getUserIdUseCase()
    }

    private lateinit var applicationContext: Context
    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    init {
        subscribeToUserList()
        getUserListAndUpdateState()
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
            leaveSessionUseCase.execute(sessionId)
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
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                commonWebsocketInteractor.subscribeUsers(sessionId)
                    .collect { result ->
                        result?.fold(
                            onSuccess = { list ->
                                if (_state.value is WaitSessionState.Content) {
                                    _state.update {
                                        WaitSessionState.Content(list.map { it.name })
                                    }
                                }
                            },
                            onError = { error ->
                                _state.update { WaitSessionState.Error(mapErrorToUiState(error)) }
                            }
                        )
                    }
            }.onFailure { error ->
                _state.update { WaitSessionState.Error(ErrorScreenState.SERVER_ERROR) }
                if (BuildConfig.DEBUG) {
                    error.printStackTrace()
                }
            }
        }
    }


    private fun getUserListAndUpdateState() {
        runSafelyUseCase(
            useCaseFlow = getSessionUseCase(sessionId),
            onSuccess = { session ->
                _state.update {
                    WaitSessionState.Content(
                        session.users
                    )
                }
            },
            onFailure = {}
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

    private companion object {
        val TAG = WaitSessionViewModel::class.simpleName
    }
}
