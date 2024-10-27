package com.davay.android.feature.sessionsmatched.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.davay.android.base.BaseViewModel
import com.davay.android.feature.sessionsmatched.domain.GetSessionsHistoryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class MatchedSessionsViewModel @Inject constructor(
    private val getSessionsHistoryUseCase: GetSessionsHistoryUseCase
) : BaseViewModel() {
    private val _state = MutableStateFlow<MatchedSessionsState>(MatchedSessionsState.Loading)
    val state = _state.asStateFlow()

    init {
        getMatchedSessions()
    }

    private fun getMatchedSessions() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                getSessionsHistoryUseCase.execute().collect { sessions ->
                    when {
                        sessions.isEmpty() -> _state.value = MatchedSessionsState.Empty
                        else -> _state.update {
                            Log.d("MatchedSessionsViewModel", "getMatchedSessions: $sessions")
                            MatchedSessionsState.Content(sessions) }
                    }
                }
            }.onFailure {
                _state.value = MatchedSessionsState.Empty
            }
        }
    }
}
