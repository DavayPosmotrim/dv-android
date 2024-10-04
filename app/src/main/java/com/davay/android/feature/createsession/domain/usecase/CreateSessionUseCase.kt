package com.davay.android.feature.createsession.domain.usecase

import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import com.davay.android.core.domain.models.Session
import com.davay.android.feature.createsession.domain.api.CreateSessionRepository
import com.davay.android.feature.createsession.domain.model.SessionType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateSessionUseCase @Inject constructor(
    private val repository: CreateSessionRepository
) {
    /**
     * Передаем имя параметра запроса и его значение
     */
    operator fun invoke(sessionType: SessionType, requestBody: List<String>): Flow<Result<Session, ErrorType>> {
        return repository.createSession(sessionType, requestBody)
    }
}