package com.davay.android.core.domain.impl

import com.davay.android.core.domain.api.GetSessionRepository
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import com.davay.android.core.domain.models.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetSessionDataAndSaveToDbUseCase @Inject constructor(
    private val repository: GetSessionRepository
) {
    operator fun invoke(sessionId: String): Flow<Result<Session, ErrorType>> = flow {
        repository.getSessionAndSaveToDb(sessionId)
    }
}