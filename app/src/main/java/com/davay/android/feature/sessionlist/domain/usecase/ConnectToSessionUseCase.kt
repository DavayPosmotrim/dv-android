package com.davay.android.feature.sessionlist.domain.usecase

import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import com.davay.android.core.domain.models.Session
import com.davay.android.feature.sessionlist.domain.api.ConnectToSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConnectToSessionUseCase @Inject constructor(
    private val repository: ConnectToSessionRepository
) {
    fun execute(sessionId: String): Flow<Result<Session, ErrorType>> {
        return repository.connectToSession(sessionId)
    }
}