package com.davay.android.core.data.impl

import android.database.sqlite.SQLiteException
import android.util.Log
import com.davay.android.BuildConfig
import com.davay.android.core.data.converters.toDomain
import com.davay.android.core.data.database.HistoryDao
import com.davay.android.core.data.network.HttpNetworkClient
import com.davay.android.core.data.network.model.getmatches.GetMatchesRequest
import com.davay.android.core.data.network.model.getmatches.GetMatchesResponse
import com.davay.android.core.data.network.model.mapToErrorType
import com.davay.android.core.domain.api.GetMatchesRepository
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.core.domain.models.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMatchesRepositoryIml @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val httpNetworkClient: HttpNetworkClient<GetMatchesRequest, GetMatchesResponse>,
    private val historyDao: HistoryDao
) : GetMatchesRepository {
    override fun getMatches(sessionId: String): Flow<Result<List<MovieDetails>, ErrorType>> = flow {
        val deviceId = userDataRepository.getUserId()
        val response = httpNetworkClient.getResponse(
            GetMatchesRequest(
                sessionId = sessionId,
                userId = deviceId
            )
        )
        when (val body = response.body) {
            is GetMatchesResponse.Session -> {
                val matchedMovieIdList = body.value
                val matchedMovies = matchedMovieIdList.mapNotNull { movie ->
                    getMovieDetails(movie.id)
                }
                emit(Result.Success(matchedMovies))
            }

            else -> {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "getMatches response body: ${response.body}")
                }
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
        val TAG = GetMatchesRepositoryIml::class.simpleName
    }
}