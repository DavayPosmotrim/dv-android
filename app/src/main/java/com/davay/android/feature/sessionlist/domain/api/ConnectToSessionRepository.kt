package com.davay.android.feature.sessionlist.domain.api

import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import com.davay.android.core.domain.models.Session
import kotlinx.coroutines.flow.Flow

interface ConnectToSessionRepository {
    fun connectToSession(sessionId: String): Flow<Result<Session, ErrorType>>
}