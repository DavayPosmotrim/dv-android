package com.davay.android.feature.matchedsession.presentation

import com.davay.android.core.domain.models.ErrorScreenState
import com.davay.android.core.domain.models.SessionWithMovies

sealed class MatchedSessionState {

    class Empty(val data: SessionWithMovies) : MatchedSessionState()
    data object Loading : MatchedSessionState()
    class Data(val data: SessionWithMovies) : MatchedSessionState()
    class Error(val errorType: ErrorScreenState) : MatchedSessionState()
}