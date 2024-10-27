package com.davay.android.core.domain.api

import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import com.davay.android.core.domain.models.Session
import com.davay.android.core.domain.models.SessionWithMovies
import kotlinx.coroutines.flow.Flow

interface SessionsHistoryRepository {
    suspend fun saveSessionsHistory(session: Session): Result<Unit, ErrorType>
    fun getSessionsHistory(): Flow<List<Session>>
    suspend fun getSessionWithMovies(session: Session): SessionWithMovies?
}