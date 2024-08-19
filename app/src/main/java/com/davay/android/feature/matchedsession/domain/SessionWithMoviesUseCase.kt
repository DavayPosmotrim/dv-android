package com.davay.android.feature.matchedsession.domain

import com.davay.android.core.domain.api.SessionsHistoryRepository
import com.davay.android.core.domain.models.SessionWithMovies
import javax.inject.Inject

class SessionWithMoviesUseCase @Inject constructor(
    private val repository: SessionsHistoryRepository
) {
    suspend fun execute(sessionId: String): SessionWithMovies? {
        return repository.getSessionWithMovies(sessionId)
    }
}