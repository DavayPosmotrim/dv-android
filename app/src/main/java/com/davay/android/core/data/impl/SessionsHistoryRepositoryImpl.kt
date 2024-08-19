package com.davay.android.core.data.impl

import com.davay.android.BuildConfig
import com.davay.android.core.data.converters.toDbEntity
import com.davay.android.core.data.converters.toDomain
import com.davay.android.core.data.database.HistoryDao
import com.davay.android.core.domain.api.SessionsHistoryRepository
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.core.domain.models.Result
import com.davay.android.core.domain.models.Session
import com.davay.android.core.domain.models.SessionWithMovies
import javax.inject.Inject

class SessionsHistoryRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao
) : SessionsHistoryRepository {

    override suspend fun saveSessionsHistory(
        session: Session,
        movies: List<MovieDetails>
    ): Result<Unit, ErrorType> =
        @Suppress("TooGenericExceptionCaught", "PrintStackTrace")
        try {
            historyDao.saveSessionWithFilms(
                session.toDbEntity(),
                movies.map { it.toDbEntity() }
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            Result.Error(ErrorType.UNKNOWN_ERROR)
        }

    override suspend fun getSessionWithMovies(sessionId: String): SessionWithMovies? =
        @Suppress("TooGenericExceptionCaught", "PrintStackTrace")
        try {
            historyDao.getSessionWithMovies(sessionId).toDomain()
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            null
        }

    override suspend fun getSessionsHistory(): List<Session>? =
        @Suppress("TooGenericExceptionCaught", "PrintStackTrace")
        try {
            historyDao.getSessions().map { it.toDomain() }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            null
        }
}