package com.davay.android.feature.createsession.presentation.createsession

import android.os.Bundle
import com.davay.android.R
import com.davay.android.base.BaseViewModel
import com.davay.android.core.domain.impl.CommonWebsocketInteractor
import com.davay.android.core.domain.impl.LeaveSessionUseCase
import com.davay.android.core.domain.models.SessionShort
import com.davay.android.core.domain.models.SessionStatus
import com.davay.android.core.domain.models.converter.toSessionShort
import com.davay.android.feature.sessionlist.presentation.ConnectToSessionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

open class CreateSessionViewModel @Inject constructor(
    private val commonWebsocketInteractor: CommonWebsocketInteractor,
    private val leaveSessionUseCase: LeaveSessionUseCase
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

    protected fun subscribeToWebsockets(sessionId: String) {
        subscribeToUsers(sessionId)
        subscribeSessionStatus(sessionId)
        subscribeSessionResult(sessionId)
        subscribeMatchesId(sessionId)
        subscribeRouletteId(sessionId)
    }

    private fun subscribeToUsers(sessionId: String) {
        runSafelyUseCase(
            useCaseFlow = commonWebsocketInteractor.subscribeUsers(sessionId),
            onFailure = { error ->
                _state.update { ConnectToSessionState.Error(mapErrorToUiState(error)) }
            },
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
            }
        )
    }

    private fun subscribeSessionStatus(sessionId: String) {
        runSafelyUseCase(
            useCaseFlow = commonWebsocketInteractor.subscribeSessionStatus(sessionId),
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
                        leaveSessionAndNavigateBack(sessionId)
                    }

                    else -> {
                        // do nothing
                    }
                }
            }
        )
    }

    private fun subscribeSessionResult(sessionId: String) {
        runSafelyUseCase(
            useCaseFlow = commonWebsocketInteractor.subscribeSessionResult(sessionId = sessionId),
            onSuccess = {},
            onFailure = { error ->
                _state.update { ConnectToSessionState.Error(mapErrorToUiState(error)) }
            }
        )
    }

    private fun subscribeMatchesId(sessionId: String) {
        runSafelyUseCase(
            useCaseFlow = commonWebsocketInteractor.subscribeMatchesId(sessionId),
            onSuccess = {},
            onFailure = { error ->
                _state.update { ConnectToSessionState.Error(mapErrorToUiState(error)) }
            }
        )
    }

    private fun subscribeRouletteId(sessionId: String) {
        runSafelyUseCase(
            useCaseFlow = commonWebsocketInteractor.subscribeRouletteId(sessionId),
            onSuccess = {},
            onFailure = { error ->
                _state.update { ConnectToSessionState.Error(mapErrorToUiState(error)) }
            }
        )
    }

    fun leaveSessionAndNavigateBack(sessionId: String) {
        _state.update { ConnectToSessionState.Loading }
        runSafelyUseCase(
            useCaseFlow = leaveSessionUseCase.execute(sessionId),
            onFailure = {
                navigateBack()
            },
            onSuccess = {
                navigateBack()
            }
        )
    }

    companion object {
        const val SESSION_DATA = "session_data"
    }
}
