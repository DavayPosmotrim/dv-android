package com.davay.android.feature.sessionsmatched.presentation

import androidx.lifecycle.viewModelScope
import com.davay.android.base.BaseViewModel
import com.davay.android.domain.models.Session
import com.davay.android.domain.models.SessionStatus
import com.davay.android.domain.models.User
import com.davay.android.feature.sessionsmatched.domain.GetSessionsHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MatchedSessionsViewModel @Inject constructor(
    private val getSessionsHistoryRepository: GetSessionsHistoryRepository
) : BaseViewModel() {
    private val _state = MutableStateFlow<MatchedSessionsState>(MatchedSessionsState.Loading)
    val state = _state.asStateFlow()

    init {
        getMatchedSessions()
    }

    private fun getMatchedSessions() {
        _state.value = MatchedSessionsState.Loading
        viewModelScope.launch {
            val list = getSessionsHistoryRepository.getSessionsHistory()
            if (list.isNullOrEmpty()) {
                _state.value = MatchedSessionsState.Empty
            } else {
                _state.value =
                    MatchedSessionsState.Content(getSessionsHistoryRepository.getSessionsHistory())
            }
        }
    }
}
