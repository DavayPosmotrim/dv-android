package com.davay.android.feature.selectmovie.data.impl

import android.database.sqlite.SQLiteException
import android.util.Log
import com.davay.android.BuildConfig
import com.davay.android.core.data.converters.toDbEntity
import com.davay.android.core.data.converters.toDomain
import com.davay.android.core.data.database.HistoryDao
import com.davay.android.core.data.database.MovieIdDao
import com.davay.android.core.data.database.entity.MovieIdEntity
import com.davay.android.core.data.network.HttpNetworkClient
import com.davay.android.core.data.network.model.mapToErrorType
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.core.domain.models.Result
import com.davay.android.feature.selectmovie.data.network.models.GetMovieRequest
import com.davay.android.feature.selectmovie.data.network.models.GetMovieResponse
import com.davay.android.feature.selectmovie.domain.api.SelectMovieRepository
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SelectMovieRepositoryImpl @Inject constructor(
    private val httpNetworkClient: HttpNetworkClient<GetMovieRequest, GetMovieResponse>,
    private val movieIdDao: MovieIdDao,
    private val historyDao: HistoryDao
) : SelectMovieRepository {
    /**
     * Метод принимает номер позиции и возвращает список MovieDetails
     */
    override fun getMovieListByPositionId(positionNumber: Int): Flow<Result<List<MovieDetails>, ErrorType>> =
        flow {
            val movies = mutableListOf<MovieDetails>()
            coroutineScope {
                val movieIdList: List<Deferred<Result<MovieDetails, ErrorType>>> =
                    getMovieIdsByPositionRange(positionNumber).map { movieId ->
                        async(Dispatchers.IO) {
                            val movie = getMovieFromDb(movieId)
                            if (movie != null) {
                                Result.Success(movie)
                            } else {
                                getMovieDetailsFromApiAndSaveToDb(movieId)
                            }
                        }
                    }

                movieIdList.awaitAll().forEach { result ->
                    when (result) {
                        is Result.Success -> {
                            movies.add(result.data)
                        }

                        is Result.Error -> {
                            emit(Result.Error(result.error))
                        }
                    }
                }
                emit(Result.Success(movies))
            }
        }

    private suspend fun getMovieIdsByPositionRange(positionNumber: Int): List<Int> {
        return try {
            movieIdDao.getMovieIdsByPositionRange(positionNumber, PAGINATION_SIZE)
        } catch (e: SQLiteException) {
            if (BuildConfig.DEBUG) {
                Log.e(
                    TAG,
                    "Error get movie ids from position: $positionNumber, exception -> ${e.localizedMessage}"
                )
            }
            emptyList()
        }
    }

    private suspend fun getMovieDetailsFromApiAndSaveToDb(movieId: Int): Result<MovieDetails, ErrorType> {
        return try {
            val response = httpNetworkClient.getResponse(GetMovieRequest.Movie(movieId))
            when (val body = response.body) {
                is GetMovieResponse.Movie -> {
                    val movieDetails = body.value.toDomain()
                    saveMovieToDatabase(movieDetails)

                    val movieFromDb = getMovieFromDb(movieId)
                    if (movieFromDb == null) {
                        Result.Error(ErrorType.UNKNOWN_ERROR)
                    } else {
                        Result.Success(movieFromDb)
                    }
                }

                else -> Result.Error(response.resultCode.mapToErrorType())
            }
        } catch (e: IOException) {
            if (BuildConfig.DEBUG) {
                Log.e(
                    TAG,
                    "Error get movie details and save to DB, ID : $movieId, exception -> ${e.localizedMessage}"
                )
            }
            Result.Error(ErrorType.UNKNOWN_ERROR)
        }
    }

    private suspend fun getMovieFromDb(movieId: Int): MovieDetails? {
        return try {
            historyDao.getMovieDetailsById(movieId)?.toDomain()
        } catch (e: SQLiteException) {
            if (BuildConfig.DEBUG) {
                Log.e(
                    TAG,
                    "Error get movie by movie ID: $movieId, exception -> ${e.localizedMessage}"
                )
            }
            null
        }
    }

    private suspend fun saveMovieToDatabase(movieDetails: MovieDetails) {
        try {
            historyDao.insertMovie(movieDetails.toDbEntity())
        } catch (e: SQLiteException) {
            if (BuildConfig.DEBUG) {
                Log.e(
                    TAG,
                    "Error save movie to DB, ID: ${movieDetails.id}, exception -> ${e.localizedMessage}"
                )
            }
        }
    }

    override suspend fun getMovieIdListSize(): Int {
        return try {
            movieIdDao.getMovieIdsCount()
        } catch (e: SQLiteException) {
            if (BuildConfig.DEBUG) {
                Log.e(
                    TAG,
                    "Error get movie ID list size, exception -> ${e.localizedMessage}"
                )
            }
            0
        }
    }

    /**
     * Метод обновляет значение лайка по позиции
     * Обновление позиции происходит всегда для предсказуемого результат
     */
    override suspend fun updateIsLikedByPosition(position: Int, isLiked: Boolean) {
        try {
            movieIdDao.updateIsLikedById(position - 1, isLiked)
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "updateIsLikedByPosition position = $position")
            }
        } catch (e: SQLiteException) {
            if (BuildConfig.DEBUG) {
                Log.e(
                    TAG,
                    "Error update Liked in movie position: $position, exception -> ${e.localizedMessage}"
                )
            }
        }
    }

    override suspend fun getMovieDetailsById(movieId: Int): MovieDetails? {
        return try {
            historyDao.getMovieDetailsById(movieId)?.toDomain()
        } catch (e: SQLiteException) {
            if (BuildConfig.DEBUG) {
                Log.e(
                    TAG,
                    "Error get movie details by id: $movieId, exception -> ${e.localizedMessage}"
                )
            }
            null
        }
    }

    private suspend fun getMovieIdByPosition(position: Int): MovieIdEntity? {
        return try {
            movieIdDao.getMovieIdByPosition(position)
        } catch (e: SQLiteException) {
            if (BuildConfig.DEBUG) {
                Log.e(
                    TAG,
                    "Error get movie ID by position: $position, exception -> ${e.localizedMessage}"
                )
            }
            null
        }
    }

    override suspend fun leaveOnlyDislikedMovieIds() {
        val movieList = getAllMovieIds()
        val dislikedMovies = movieList.filter {
            it.isLiked.not()
        }
        clearAndResetTable()
        var movieId = 1
        dislikedMovies.forEach { movieIdEntity ->
            try {
                movieIdDao.insertMovieId(movieIdEntity.copy(id = movieId))
                movieId++
            } catch (e: SQLiteException) {
                if (BuildConfig.DEBUG) {
                    Log.e(
                        TAG,
                        "Error insert movie ID to DB, ID: $movieId, exception -> ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    private suspend fun getAllMovieIds(): List<MovieIdEntity> {
        return try {
            movieIdDao.getAllMovieIds()
        } catch (e: SQLiteException) {
            if (BuildConfig.DEBUG) {
                Log.e(
                    TAG,
                    "Error get movie ID list, exception -> ${e.localizedMessage}"
                )
            }
            emptyList()
        }
    }

    private suspend fun clearAndResetTable() {
        try {
            movieIdDao.clearAndResetTable()
        } catch (e: SQLiteException) {
            if (BuildConfig.DEBUG) {
                Log.e(
                    TAG,
                    "Error clear and reset movie ID table, exception -> ${e.localizedMessage}"
                )
            }
        }
    }

    private companion object {
        /**
         * Размер подгрузки фильмов, при изменении так же учитывать значение в SelectMovieViewModel.
         * PAGINATION_SIZE в репозитории должен быть больше либо равен PRELOAD_SIZE в SelectMovieViewModel
         */
        const val PAGINATION_SIZE = 20
        val TAG = SelectMovieRepositoryImpl::class.simpleName
    }
}