package com.davay.android.core.presentation.states

import com.davay.android.core.domain.models.ErrorScreenState
import com.davay.android.core.domain.models.SessionStatus

sealed interface SessionState {
    class Status(val status: SessionStatus?) : SessionState
    class Error(val error: ErrorScreenState) : SessionState
}