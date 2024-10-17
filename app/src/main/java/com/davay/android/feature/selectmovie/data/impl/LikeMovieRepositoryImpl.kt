package com.davay.android.feature.selectmovie.data.impl

import android.database.sqlite.SQLiteException
import android.util.Log
import com.davay.android.BuildConfig
import com.davay.android.core.data.database.MovieIdDao
import com.davay.android.core.data.network.HttpNetworkClient
import com.davay.android.core.data.network.model.mapToErrorType
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import com.davay.android.feature.selectmovie.data.network.models.LikeMovieRequest
import com.davay.android.feature.selectmovie.data.network.models.LikeMovieResponse
import com.davay.android.feature.selectmovie.domain.api.LikeMovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LikeMovieRepositoryImpl @Inject constructor(
    userDataRepository: UserDataRepository,
    private val httpNetworkClient: HttpNetworkClient<LikeMovieRequest, LikeMovieResponse>,
    private val movieIdDao: MovieIdDao
) : LikeMovieRepository {

    private val deviceId = userDataRepository.getUserId()

    override fun likeMovie(
        moviePosition: Int,
        sessionId: String
    ): Flow<Result<LikeMovieResponse, ErrorType>> = flow {
        val position = moviePosition - 1 // поправка позиции
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "position = $position")
        }
        val movieId = try {
            movieIdDao.getMovieIdByPosition(position)?.movieId ?: 0
        } catch (e: SQLiteException) {
            if (BuildConfig.DEBUG) {
                Log.e(
                    TAG,
                    "Error get movie id by position: $position, exception -> ${e.localizedMessage}"
                )
            }
            0
        }

        val response = httpNetworkClient.getResponse(
            LikeMovieRequest.Like(
                movieId = movieId,
                sessionId = sessionId,
                userId = deviceId
            )
        )
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "response = ${response.body}")
        }

        when (val body = response.body) {
            is LikeMovieResponse -> {
                emit(Result.Success(body))
            }

            else -> {
                emit(Result.Error(response.resultCode.mapToErrorType()))
            }
        }
    }

    override fun dislikeMovie(
        moviePosition: Int,
        sessionId: String
    ): Flow<Result<LikeMovieResponse, ErrorType>> = flow {
        val movieId = try {
            movieIdDao.getMovieIdByPosition(moviePosition)?.movieId ?: 0
        } catch (e: SQLiteException) {
            if (BuildConfig.DEBUG) {
                Log.e(
                    TAG,
                    "Error get movie id by position: $moviePosition, exception -> ${e.localizedMessage}"
                )
            }
            0
        }

        val response = httpNetworkClient.getResponse(
            LikeMovieRequest.Dislike(
                movieId = movieId,
                sessionId = sessionId,
                userId = deviceId
            )
        )

        when (val body = response.body) {
            is LikeMovieResponse -> {
                emit(Result.Success(body))
            }

            else -> {
                emit(Result.Error(response.resultCode.mapToErrorType()))
            }
        }
    }

    private companion object {
        val TAG = LikeMovieRepositoryImpl::class.simpleName
    }
}