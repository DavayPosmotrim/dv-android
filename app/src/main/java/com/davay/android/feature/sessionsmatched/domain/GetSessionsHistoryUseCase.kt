package com.davay.android.feature.sessionsmatched.domain

import com.davay.android.core.domain.api.SessionsHistoryRepository
import com.davay.android.core.domain.models.Session
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSessionsHistoryUseCase @Inject constructor(
    private val repository: SessionsHistoryRepository
) {
    fun execute(): Flow<List<Session>> {
        return repository.getSessionsHistory()
    }
}