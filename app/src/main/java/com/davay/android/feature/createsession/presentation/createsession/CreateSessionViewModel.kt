package com.davay.android.feature.createsession.presentation.createsession

import android.os.Bundle
import androidx.lifecycle.viewModelScope
import com.davay.android.BuildConfig
import com.davay.android.R
import com.davay.android.base.BaseViewModel
import com.davay.android.core.domain.impl.CommonWebsocketInteractor
import com.davay.android.core.domain.models.ErrorScreenState
import com.davay.android.core.domain.models.SessionShort
import com.davay.android.core.domain.models.SessionStatus
import com.davay.android.core.domain.models.converter.toSessionShort
import com.davay.android.feature.sessionlist.presentation.ConnectToSessionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

open class CreateSessionViewModel @Inject constructor(
    private val commonWebsocketInteractor: CommonWebsocketInteractor,
) : BaseViewModel() {
    private val _state = MutableStateFlow<ConnectToSessionState>(ConnectToSessionState.Loading)
    val state = _state.asStateFlow()

    protected fun navigateToWaitSession(session: SessionShort) {
        val sessionJson = Json.encodeToString(session)
        val bundle = Bundle().apply {
            putString(SESSION_DATA, sessionJson)
        }
        navigate(
            R.id.action_createSessionFragment_to_waitSessionFragment,
            bundle
        )
    }

    protected fun subscribeToWebsocketsAndUpdateSessionId(sessionId: String) {
        commonWebsocketInteractor.updateSessionId(sessionId)
        viewModelScope.launch {
            subscribeToUsers()
            subscribeSessionStatus()
            subscribeSessionResult()
            subscribeMatchesId()
            subscribeRouletteId()
        }
    }

    private fun subscribeToUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                commonWebsocketInteractor.subscribeUsers()
                    .collect { result ->
                        result?.fold(
                            onSuccess = { list ->
                                if (_state.value is ConnectToSessionState.Content) {
                                    _state.update {
                                        ConnectToSessionState.Content(
                                            (_state.value as ConnectToSessionState.Content)
                                                .session
                                                .copy(users = list.map { it.name })
                                        )
                                    }
                                }
                            },
                            onError = { error ->
                                _state.update { ConnectToSessionState.Error(mapErrorToUiState(error)) }
                            }
                        )
                    }
            }.onFailure { error ->
                _state.update { ConnectToSessionState.Error(ErrorScreenState.SERVER_ERROR) }
                if (BuildConfig.DEBUG) {
                    error.printStackTrace()
                }
            }
        }
    }

    private fun subscribeSessionStatus() {
        runSafelyUseCase(
            useCaseFlow = commonWebsocketInteractor.subscribeSessionStatus(),
            onFailure = { error ->
                _state.update { ConnectToSessionState.Error(mapErrorToUiState(error)) }
            },
            onSuccess = { status ->
                when (status) {
                    SessionStatus.VOTING -> {
                        val session =
                            (_state.value as ConnectToSessionState.Content).session.toSessionShort()
                        val sessionJson = Json.encodeToString(session)
                        val bundle = Bundle().apply {
                            putString(SESSION_DATA, sessionJson)
                        }
                        navigate(
                            R.id.action_waitSessionFragment_to_selectMovieFragment,
                            bundle
                        )
                    }

                    SessionStatus.CLOSED -> {
                        navigateBack()
                    }

                    else -> {
                        // do nothing
                    }
                }
            }
        )
    }

    private fun subscribeSessionResult() {
        runSafelyUseCase(
            useCaseFlow = commonWebsocketInteractor.subscribeSessionResult(),
            onSuccess = {},
            onFailure = { error ->
                _state.update { ConnectToSessionState.Error(mapErrorToUiState(error)) }
            }
        )
    }

    private fun subscribeMatchesId() {
        runSafelyUseCase(
            useCaseFlow = commonWebsocketInteractor.subscribeMatchesId(),
            onSuccess = {},
            onFailure = { error ->
                _state.update { ConnectToSessionState.Error(mapErrorToUiState(error)) }
            }
        )
    }

    private fun subscribeRouletteId() {
        runSafelyUseCase(
            useCaseFlow = commonWebsocketInteractor.subscribeRouletteId(),
            onSuccess = {},
            onFailure = { error ->
                _state.update { ConnectToSessionState.Error(mapErrorToUiState(error)) }
            }
        )
    }

    companion object {
        const val SESSION_DATA = "session_data"
    }
}
