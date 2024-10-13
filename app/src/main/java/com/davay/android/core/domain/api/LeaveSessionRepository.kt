package com.davay.android.core.domain.api

import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import kotlinx.coroutines.flow.Flow

interface LeaveSessionRepository {
    fun leaveSession(sessionId: String): Flow<Result<String, ErrorType>>
}