package com.davay.android.core.data.impl

import com.davay.android.BuildConfig
import com.davay.android.core.data.converters.toDbEntity
import com.davay.android.core.data.converters.toDomain
import com.davay.android.core.data.database.HistoryDao
import com.davay.android.core.data.database.entity.MovieDetailsEntity
import com.davay.android.core.domain.api.SessionsHistoryRepository
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import com.davay.android.core.domain.models.Session
import com.davay.android.core.domain.models.SessionWithMovies
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SessionsHistoryRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao
) : SessionsHistoryRepository {

    override suspend fun saveSessionsHistory(session: Session): Result<Unit, ErrorType> =
        @Suppress("TooGenericExceptionCaught", "PrintStackTrace")
        try {
            val movies: List<MovieDetailsEntity> =
                session.matchedMovieIdList.mapNotNull { historyDao.getMovieDetailsById(it) }
            historyDao.saveSessionWithFilms(
                session.toDbEntity(),
                movies
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            Result.Error(ErrorType.UNKNOWN_ERROR)
        }

    override suspend fun getSessionWithMovies(session: Session): SessionWithMovies? =
        @Suppress("TooGenericExceptionCaught", "PrintStackTrace")
        try {
            historyDao.getSessionWithMovies(session.id).toDomain()
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            null
        }

    override fun getSessionsHistory(): Flow<List<Session>> {
        @Suppress("TooGenericExceptionCaught", "PrintStackTrace")
        return try {
            historyDao.getSessions().map { list ->
                list.map { session ->
                    session.toDomain()
                }.reversed()
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            flow {
                emit(emptyList())
            }
        }
    }
}