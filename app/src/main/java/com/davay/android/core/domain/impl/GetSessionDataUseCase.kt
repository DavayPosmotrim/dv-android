package com.davay.android.core.domain.impl

import com.davay.android.core.domain.api.LeaveSessionRepository
import com.davay.android.core.domain.api.SessionDataRepository
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import com.davay.android.core.domain.models.Session
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSessionDataUseCase @Inject constructor(
    private val sessionDataRepository: SessionDataRepository
) {
    operator fun invoke(sessionId: String): Flow<Result<Session, ErrorType>> {
        return sessionDataRepository.getSessionData(sessionId)
    }
}