package com.davay.android.core.data

import android.database.sqlite.SQLiteException
import android.util.Log
import com.davay.android.BuildConfig
import com.davay.android.core.data.database.MovieIdDao
import com.davay.android.core.data.database.entity.MovieIdEntity

class MovieIdListToDbSaverImpl : MovieIdListToDbSaver {

    override suspend fun saveMovieIdListToDb(idList: List<Int>, movieIdDao: MovieIdDao) {
        try {
            clearAndResetIdsTable(movieIdDao)

            idList.forEach { id ->
                insertMovieToDb(id, movieIdDao)
            }
        } catch (e: SQLiteException) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Database operation failed: ${e.localizedMessage}", e)
            }
        }
    }

    private suspend fun insertMovieToDb(movieId: Int, movieIdDao: MovieIdDao) {
        try {
            movieIdDao.insertMovieId(MovieIdEntity(movieId = movieId))
        } catch (e: SQLiteException) {
            if (BuildConfig.DEBUG) {
                Log.e(
                    TAG,
                    "Error inserting movie ID: $movieId, exception -> ${e.localizedMessage}"
                )
            }
        }
    }

    private suspend fun clearAndResetIdsTable(movieIdDao: MovieIdDao) {
        val movieIdCount = try {
            movieIdDao.getMovieIdsCount()
        } catch (e: SQLiteException) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Error in getMovieIdsCount, exception -> ${e.localizedMessage}")
            }
            0
        }

        if (movieIdCount > 0) {
            try {
                movieIdDao.clearAndResetTable()
            } catch (e: SQLiteException) {
                if (BuildConfig.DEBUG) {
                    Log.e(
                        TAG,
                        "Error in clear and reset table, exception -> ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    private companion object {
        val TAG = MovieIdListToDbSaverImpl::class.simpleName
    }
}