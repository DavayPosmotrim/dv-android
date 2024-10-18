package com.davay.android.feature.roulette.data.impl

import com.davay.android.core.data.converters.toDomain
import com.davay.android.core.data.database.HistoryDao
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.core.domain.models.Result
import com.davay.android.feature.roulette.domain.api.RouletteMoviesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RouletteMoviesRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao
) : RouletteMoviesRepository {
    @Suppress("LabeledExpression")
    override fun getMoviesByIdList(idList: List<Int>): Flow<Result<List<MovieDetails>, ErrorType>> =
        flow {
            val movies = mutableListOf<MovieDetails>()
            runCatching {
                idList.forEach { id ->
                    val movie = historyDao.getMovieDetailsById(id)
                    if (movie == null) {
                        emit(Result.Error(ErrorType.UNKNOWN_ERROR))
                        return@flow
                    } else {
                        movies.add(movie.toDomain())
                    }
                }
                emit(Result.Success(movies))
            }.onFailure {
                emit(Result.Error(ErrorType.UNKNOWN_ERROR))
            }
        }
}
