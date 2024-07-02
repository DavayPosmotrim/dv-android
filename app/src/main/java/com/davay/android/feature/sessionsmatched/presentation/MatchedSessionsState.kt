package com.davay.android.feature.sessionsmatched.presentation

import com.davay.android.domain.models.ErrorType
import com.davay.android.domain.models.Session

sealed class MatchedSessionsState {
    data object Loading : MatchedSessionsState()
    data object Empty : MatchedSessionsState()
    data class Content(val sessionsList: List<Session>) : MatchedSessionsState()
    data class Error(val errorType: ErrorType) : MatchedSessionsState()
}