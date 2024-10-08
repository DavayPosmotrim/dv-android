package com.davay.android.core.domain.api

import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import com.davay.android.core.domain.models.Session
import kotlinx.coroutines.flow.Flow

interface SessionDataRepository {
    fun getSessionData(sessionId: String): Flow<Result<Session, ErrorType>>
}