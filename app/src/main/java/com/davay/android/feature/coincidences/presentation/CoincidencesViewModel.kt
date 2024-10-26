package com.davay.android.feature.coincidences.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.davay.android.BuildConfig
import com.davay.android.base.BaseViewModel
import com.davay.android.core.domain.impl.CommonWebsocketInteractor
import com.davay.android.core.domain.impl.GetMatchesUseCase
import com.davay.android.core.domain.impl.GetSessionDataAndSaveToDbUseCase
import com.davay.android.core.domain.impl.LeaveSessionUseCase
import com.davay.android.core.domain.models.ErrorScreenState
import com.davay.android.core.domain.models.Result
import com.davay.android.core.domain.models.SessionStatus
import com.davay.android.feature.coincidences.domain.api.CoincidencesInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class CoincidencesViewModel @Inject constructor(
    private val coincidencesInteractor: CoincidencesInteractor,
    private val getMatchesUseCase: GetMatchesUseCase,
    private val commonWebsocketInteractor: CommonWebsocketInteractor,
    private val leaveSessionUseCase: LeaveSessionUseCase,
    private val getSessionDataAndSaveToDbUseCase: GetSessionDataAndSaveToDbUseCase
) : BaseViewModel() {

    private val _state: MutableStateFlow<CoincidencesState> =
        MutableStateFlow(CoincidencesState.Loading)
    val state
        get() = _state.asStateFlow()

    private val _sessionStatusState = MutableStateFlow(SessionStatus.VOTING)
    val sessionStatusState
        get() = _sessionStatusState.asStateFlow()

    init {
        getCoincidences()
        subscribeSessionStatus()
    }

    fun getCoincidences() {
        runSafelyUseCase(
            useCaseFlow = getMatchesUseCase(),
            onSuccess = { movieList ->
                if (movieList.isEmpty()) {
                    _state.update {
                        CoincidencesState.Error(ErrorScreenState.EMPTY)
                    }
                } else {
                    _state.update {
                        CoincidencesState.Content(movieList)
                    }
                }
            },
            onFailure = { error ->
                _state.update {
                    CoincidencesState.Error(mapErrorToUiState(error))
                }
            }
        )
    }

    fun isFirstTimeLaunch(): Boolean = coincidencesInteractor.isFirstTimeLaunch()

    fun markFirstTimeLaunch() {
        coincidencesInteractor.markFirstTimeLaunch()
    }


    fun leaveSessionAndNavigateToHistory() {
        val action = CoincidencesFragmentDirections.actionCoincidencesFragmentToMatchedSessionListFragment()
        disconnect()
        navigate(action)
    }

    fun getSessionDataAndSaveToDb() {
        Log.i(TAG, "getSessionDataAndSaveToDb")
        val sessionId = commonWebsocketInteractor.sessionId
        runCatching {
            getSessionDataAndSaveToDbUseCase(sessionId)
        }.onFailure {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "Failure on get session data and save to db")
            }
        }.onSuccess {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "Success on get session data and save to db")
            }
        }
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

    private companion object {
        val TAG = CoincidencesViewModel::class.simpleName
    }
}