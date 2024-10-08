package com.davay.android.feature.waitsession.presentation

import com.davay.android.core.domain.models.ErrorScreenState

sealed interface WaitSessionState {
    class Error(val errorType: ErrorScreenState) : WaitSessionState
    class Content(val users: List<String>) : WaitSessionState
}