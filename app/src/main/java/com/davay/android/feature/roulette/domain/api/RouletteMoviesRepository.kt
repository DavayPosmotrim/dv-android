package com.davay.android.feature.roulette.domain.api

import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.core.domain.models.Result
import kotlinx.coroutines.flow.Flow

interface RouletteMoviesRepository {
    fun getMoviesByIdList(idList: List<Int>): Flow<Result<List<MovieDetails>, ErrorType>>
}