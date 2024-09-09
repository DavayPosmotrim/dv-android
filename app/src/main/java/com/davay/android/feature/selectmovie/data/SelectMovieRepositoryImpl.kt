package com.davay.android.feature.selectmovie.data

import android.util.Log
import com.davay.android.core.data.converters.toDbEntity
import com.davay.android.core.data.converters.toDomain
import com.davay.android.core.data.database.HistoryDao
import com.davay.android.core.data.database.MovieIdDao
import com.davay.android.core.data.network.HttpNetworkClient
import com.davay.android.core.data.network.model.mapToErrorType
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.core.domain.models.Result
import com.davay.android.feature.selectmovie.data.network.GetMovieRequest
import com.davay.android.feature.selectmovie.data.network.GetMovieResponse
import com.davay.android.feature.selectmovie.domain.api.SelectMovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SelectMovieRepositoryImpl @Inject constructor(
    private val httpNetworkClient: HttpNetworkClient<GetMovieRequest, GetMovieResponse>,
    private val movieIdDao: MovieIdDao,
    private val historyDao: HistoryDao
) : SelectMovieRepository {
    /**
     * Метод принимает номер позиции и возвращает список MovieDetails
     */
    @Suppress("LabeledExpression")
    override fun getMovieListByPositionId(positionNumber: Int): Flow<Result<List<MovieDetails>, ErrorType>> =
        flow {
            val movies = mutableListOf<MovieDetails>()

            movieIdDao.getMovieIdsByPositionRange(positionNumber, PAGINATION_SIZE)
                .forEach { movieId ->
                    val movie = historyDao.getMovieDetailsById(movieId)?.toDomain()
                    if (movie == null) {
                        getMovieDetailsFromApiAndSaveToDb(movieId).collect { result ->
                            when (result) {
                                is Result.Success -> {
                                    movies.add(result.data)
                                    Log.i("SelectMovieRepositoryImpl", result.data.name)
                                }

                                is Result.Error -> {
                                    emit(Result.Error(result.error))
                                    return@collect
                                }
                            }
                        }
                    } else {
                        movies.add(movie)
                    }
                }

            emit(Result.Success(movies))
        }

    private fun getMovieDetailsFromApiAndSaveToDb(movieId: Int): Flow<Result<MovieDetails, ErrorType>> =
        flow {
            val response = httpNetworkClient.getResponse(GetMovieRequest.Movie(movieId))
            when (val body = response.body) {
                is GetMovieResponse.Movie -> {
                    val movieDetails = body.value.toDomain(movieId)
                    saveMovieToDatabase(movieDetails)
                    emit(Result.Success(movieDetails))
                }

                else -> emit(Result.Error(response.resultCode.mapToErrorType()))
            }
        }

    private suspend fun saveMovieToDatabase(movieDetails: MovieDetails) {
        withContext(Dispatchers.IO) {
            historyDao.insertMovie(movieDetails.toDbEntity())
        }
    }

    override suspend fun getMovieIdListSize(): Int {
        return movieIdDao.getMovieIdsCount()
    }

    /**
     * Метод учитывает условие когда запрашиваемая позиция выходит за пределы списка фильмов
     */
    override suspend fun updateIsLikedByPosition(position: Int, isLiked: Boolean) {
        if (movieIdDao.getMovieIdByPosition(position) == null) {
            val previousPosition = position - 1
            movieIdDao.getMovieIdByPosition(previousPosition).let { movieIdEntity ->
                if (movieIdEntity?.isLiked == isLiked.not()) {
                    movieIdDao.updateIsLikedById(position, isLiked)
                }
            }
        } else {
            movieIdDao.getMovieIdByPosition(position).let { movieIdEntity ->
                if (movieIdEntity?.isLiked == isLiked.not()) {
                    movieIdDao.updateIsLikedById(position, isLiked)
                }
            }
        }
    }

    override suspend fun leaveOnlyDislikedMovieIds() {
        val movieList = movieIdDao.getAllMovieIds()
        val dislikedMovies = movieList.filter {
            it.isLiked.not()
        }
        movieIdDao.clearAndResetTable()
        var movieId = 1
        dislikedMovies.forEach { movieIdEntity ->
            movieIdDao.insertMovieId(movieIdEntity.copy(id = movieId))
            movieId++
        }
    }

    private companion object {
        /**
         * Размер подгрузки фильмов, при изменении так же учитывать значение в SelectMovieViewModel.
         * PAGINATION_SIZE в репозитории должен быть больше либо равен PAGINATION_SIZE в SelectMovieViewModel
         */
        const val PAGINATION_SIZE = 20
    }
}