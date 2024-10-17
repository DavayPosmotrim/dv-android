package com.davay.android.feature.coincidences.data.impl

import android.database.sqlite.SQLiteException
import android.util.Log
import com.davay.android.BuildConfig
import com.davay.android.core.data.converters.toDomain
import com.davay.android.core.data.database.HistoryDao
import com.davay.android.core.data.network.HttpNetworkClient
import com.davay.android.core.data.network.model.mapToErrorType
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.core.domain.lounchcontrol.api.FirstTimeFlagRepository
import com.davay.android.core.domain.lounchcontrol.api.FirstTimeFlagStorage
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.core.domain.models.Result
import com.davay.android.feature.coincidences.data.network.models.GetSessionRequest
import com.davay.android.feature.coincidences.data.network.models.GetSessionResponse
import com.davay.android.feature.coincidences.domain.api.CoincidencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CoincidencesRepositoryImpl @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val httpNetworkClient: HttpNetworkClient<GetSessionRequest, GetSessionResponse>,
    private val historyDao: HistoryDao,
    private val firstTimeFlagStorage: FirstTimeFlagStorage
) : FirstTimeFlagRepository, CoincidencesRepository {
    override fun isFirstTimeLaunch(): Boolean {
        return firstTimeFlagStorage.isFirstTimeLaunch()
    }

    override fun markFirstTimeLaunch() {
        firstTimeFlagStorage.markFirstTimeLaunch()
    }

    override fun getMatches(sessionId: String): Flow<Result<List<MovieDetails>, ErrorType>> = flow {
        val deviceId = userDataRepository.getUserId()
        val response = httpNetworkClient.getResponse(
            GetSessionRequest(
                sessionId = sessionId,
                userId = deviceId
            )
        )
        when (val body = response.body) {
            is GetSessionResponse.Session -> {
                val matchedMovieIdList = body.value.matchedMovieIdList
                val matchedMovies = matchedMovieIdList.mapNotNull { movieId ->
                    getMovieDetails(movieId)
                }
                emit(Result.Success(matchedMovies))
            }

            else -> {
                emit(Result.Error(response.resultCode.mapToErrorType()))
            }
        }
    }

    private suspend fun getMovieDetails(movieId: Int): MovieDetails? {
        return try {
            historyDao.getMovieDetailsById(movieId)?.toDomain()
        } catch (e: SQLiteException) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "getMovieDetails: ${e.localizedMessage}")
            }
            null
        }
    }

    private companion object {
        val TAG = CoincidencesRepositoryImpl::class.simpleName
    }
}