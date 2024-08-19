package com.davay.android.core.domain.api

import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.core.domain.models.Result
import com.davay.android.core.domain.models.Session
import com.davay.android.core.domain.models.SessionWithMovies

interface SessionsHistoryRepository {
    suspend fun saveSessionsHistory(
        session: Session,
        movies: List<MovieDetails>
    ): Result<Unit, ErrorType>

    suspend fun getSessionsHistory(): List<Session>?
    suspend fun getSessionWithMovies(sessionId: String): SessionWithMovies?
}