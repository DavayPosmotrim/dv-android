package com.davay.android.core.data

import com.davay.android.core.data.database.MovieIdDao

interface MovieIdListToDbSaver {
    suspend fun saveMovieIdListToDb(idList: List<Int>, movieIdDao: MovieIdDao)
}