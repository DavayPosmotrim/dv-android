package com.davay.android.feature.selectmovie.domain.api

import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import com.davay.android.feature.selectmovie.data.network.models.LikeMovieResponse
import kotlinx.coroutines.flow.Flow

interface LikeMovieRepository {

    fun likeMovie(moviePosition: Int, sessionId: String): Flow<Result<LikeMovieResponse, ErrorType>>

    fun dislikeMovie(moviePosition: Int, sessionId: String): Flow<Result<LikeMovieResponse, ErrorType>>
}