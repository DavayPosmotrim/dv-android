package com.davay.android.feature.waitsession.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.davay.android.BuildConfig
import com.davay.android.R
import com.davay.android.base.BaseViewModel
import com.davay.android.core.domain.impl.CommonWebsocketInteractor
import com.davay.android.core.domain.impl.LeaveSessionUseCase
import com.davay.android.core.domain.models.Result
import com.davay.android.core.domain.models.UserDataFields
import com.davay.android.core.domain.usecases.GetUserDataUseCase
import com.davay.android.feature.waitsession.domain.SetSessionStatusVotingUseCase
import com.davay.android.feature.waitsession.domain.api.WaitSessionOnBoardingInteractor
import com.davay.android.feature.waitsession.domain.models.WaitSessionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Тут нас интересует только вебсокет с подпиской на список юзеров, так как статус сессии меняет
 * только организатор, остальные сообщения нужны на других экранах
 */
class WaitSessionViewModel @Inject constructor(
    private val waitSessionOnBoardingInteractor: WaitSessionOnBoardingInteractor,
    private val commonWebsocketInteractor: CommonWebsocketInteractor,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val leaveSessionUseCase: LeaveSessionUseCase,
    private val setSessionStatusVotingUseCase: SetSessionStatusVotingUseCase
) : BaseViewModel() {
    private val _state = MutableStateFlow<WaitSessionState>(WaitSessionState.Content(emptyList()))
    val state
        get() = _state.asStateFlow()

    init {
        subscribeToUsersState()
    }

    fun isFirstTimeLaunch(): Boolean {
        return waitSessionOnBoardingInteractor.isFirstTimeLaunch()
    }

    fun markFirstTimeLaunch() {
        waitSessionOnBoardingInteractor.markFirstTimeLaunch()
    }

    /**
     * Метод необходим для обхода ошибки при возврате назад на экран создания сессии после
     * смены конфигурации устройства
     */
    fun navigateToCreateSessionAndUnsubscribeWebSockets() {
        val sessionId = commonWebsocketInteractor.sessionId
        viewModelScope.launch {
            runCatching {
                commonWebsocketInteractor.unsubscribeWebsockets()
            }.onFailure {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "unsubscribeWebsockets: error -> $it")
                }
            }
            leaveSessionUseCase.execute(sessionId)
        }
        clearBackStackToMainAndNavigate(R.id.action_mainFragment_to_createSessionFragment)
    }

    fun navigateToNextScreen() {
        runSafelyUseCase(
            useCaseFlow = setSessionStatusVotingUseCase(),
            onSuccess = {
                navigate(R.id.action_waitSessionFragment_to_selectMovieFragment)
            },
            onFailure = { error ->
                _state.update {
                    WaitSessionState.Error(mapErrorToUiState(error))
                }
            }
        )
    }

    private fun subscribeToUsersState() {
        val userName = getUserDataUseCase.getUserData(UserDataFields.UserName())
        viewModelScope.launch {
            commonWebsocketInteractor.getUsers().collect { result ->
                when (result) {
                    is Result.Success -> {
                        val users = result.data.map { it.name }
                        _state.value = WaitSessionState.Content(sortUserList(users, userName))
                    }

                    is Result.Error -> {
                        _state.value = WaitSessionState.Error(mapErrorToUiState(result.error))
                    }

                    null -> _state.value = WaitSessionState.Content(listOf(userName))
                }
            }
        }
    }

    /**
     * В списке юзеров перемещаем пользователя на первое место, т.к. в адаптере производится
     * конкатенация к первому элементу
     */
    private fun sortUserList(users: List<String>, userName: String): List<String> {
        val userList = users.toMutableList()
        val iterator = userList.iterator()

        while (iterator.hasNext()) {
            val user = iterator.next()
            if (user == userName) {
                iterator.remove()
                userList.add(0, user)
                break
            }
        }
        return userList
    }

    private companion object {
        val TAG = WaitSessionViewModel::class.simpleName
    }
}