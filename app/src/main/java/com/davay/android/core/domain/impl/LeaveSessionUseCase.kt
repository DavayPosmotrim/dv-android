package com.davay.android.core.domain.impl

import com.davay.android.core.domain.api.LeaveSessionRepository
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LeaveSessionUseCase @Inject constructor(
    private val leaveSessionRepository: LeaveSessionRepository
) {
    fun execute(sessionId: String): Flow<Result<String, ErrorType>> {
        return leaveSessionRepository.leaveSession(sessionId)
    }
}