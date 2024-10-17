package com.davay.android.feature.roulette.domain.impl

import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.core.domain.models.Result
import com.davay.android.feature.roulette.domain.api.RouletteMoviesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RouletteMoviesUseCase @Inject constructor(
    private var rouletteMoviesRepository: RouletteMoviesRepository
) {
    fun getMoviesByIdList(idList: List<Int>): Flow<Result<List<MovieDetails>, ErrorType>> {
        return rouletteMoviesRepository.getMoviesByIdList(idList)
    }
}