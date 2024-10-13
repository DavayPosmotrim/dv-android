package com.davay.android.feature.sessionlist.presentation

import com.davay.android.core.domain.models.ErrorScreenState
import com.davay.android.core.domain.models.Session

sealed interface ConnectToSessionState {
    data object Loading : ConnectToSessionState
    class Error(val errorType: ErrorScreenState) : ConnectToSessionState
    class Content(val session: Session) : ConnectToSessionState
}