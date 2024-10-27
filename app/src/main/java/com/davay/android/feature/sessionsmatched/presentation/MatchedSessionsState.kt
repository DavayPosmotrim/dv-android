package com.davay.android.feature.sessionsmatched.presentation

import com.davay.android.core.domain.models.Session

sealed class MatchedSessionsState {
    data object Loading : MatchedSessionsState()
    data object Empty : MatchedSessionsState()
    data class Content(val sessionsList: List<Session>) : MatchedSessionsState()
}