package com.davay.android.feature.selectmovie.domain.api

import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.core.domain.models.Result
import kotlinx.coroutines.flow.Flow

interface SelectMovieRepository {
    /**
     * Метод принимает номер позиции и возвращает детали о фильме
     */
    fun getMovieListByPositionId(positionNumber: Int): Flow<Result<List<MovieDetails>, ErrorType>>

    suspend fun getMovieIdListSize(): Int

    suspend fun leaveOnlyDislikedMovieIds()

    suspend fun updateIsLikedByPosition(position: Int, isLiked: Boolean)

    suspend fun getMovieDetailsById(movieId: Int): MovieDetails?
}