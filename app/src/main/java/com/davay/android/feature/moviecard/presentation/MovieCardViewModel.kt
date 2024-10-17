package com.davay.android.feature.moviecard.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.davay.android.BuildConfig
import com.davay.android.R
import com.davay.android.base.BaseViewModel
import com.davay.android.core.domain.impl.CommonWebsocketInteractor
import com.davay.android.core.domain.impl.LeaveSessionUseCase
import com.davay.android.core.domain.models.Result
import com.davay.android.core.domain.models.SessionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieCardViewModel @Inject constructor(
    private val commonWebsocketInteractor: CommonWebsocketInteractor,
    private val leaveSessionUseCase: LeaveSessionUseCase,
) : BaseViewModel() {

    private val _sessionStatusState = MutableStateFlow(SessionStatus.VOTING)
    val sessionStatusState
        get() = _sessionStatusState.asStateFlow()

    init {
        subscribeSessionStatus()
    }

    private fun subscribeSessionStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            commonWebsocketInteractor.getSessionStatus().collect { result ->
                when (result) {
                    is Result.Success -> {
                        if (_sessionStatusState.value != SessionStatus.ROULETTE) {
                            _sessionStatusState.update {
                                result.data
                            }
                        }
                    }

                    is Result.Error -> {
                        _sessionStatusState.update {
                            SessionStatus.VOTING
                        }
                    }

                    null -> {
                        _sessionStatusState.update {
                            SessionStatus.VOTING
                        }
                    }
                }
            }
        }
    }

    fun leaveSessionAndNavigateToHistory() {
        disconnect()
        clearBackStackToMainAndNavigate(R.id.action_mainFragment_to_matchedSessionListFragment)
    }

    private fun disconnect() {
        viewModelScope.launch(Dispatchers.IO) {
            val sessionId = commonWebsocketInteractor.sessionId
            runSafelyUseCase(
                useCaseFlow = leaveSessionUseCase.execute(sessionId),
                onSuccess = {},
                onFailure = { error ->
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "Error on leave session $sessionId, error -> $error")
                    }
                }
            )
            commonWebsocketInteractor.unsubscribeWebsockets()
        }
    }

    private companion object {
        val TAG = MovieCardViewModel::class.simpleName
    }
}