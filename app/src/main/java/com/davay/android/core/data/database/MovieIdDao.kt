package com.davay.android.core.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.davay.android.core.data.database.entity.MovieIdEntity

@Dao
interface MovieIdDao {

    @Upsert
    suspend fun insertMovieId(movieIdEntity: MovieIdEntity)

    @Query("SELECT * FROM movie_ids")
    suspend fun getAllMovieIds(): List<MovieIdEntity>

    @Query("DELETE FROM movie_ids")
    suspend fun clearMovieIdsTable()

    /**
     * Сброс автогенерируемых id
     */
    @Query("DELETE FROM sqlite_sequence WHERE name='movie_ids'")
    suspend fun resetAutoIncrement()

    @Transaction
    suspend fun clearAndResetTable() {
        clearMovieIdsTable()
        resetAutoIncrement()
    }

    @Query("SELECT * FROM movie_ids LIMIT 1 OFFSET :position")
    suspend fun getMovieIdByPosition(position: Int): MovieIdEntity?

    @Query("UPDATE movie_ids SET is_liked = :isLiked WHERE id = :position")
    suspend fun updateIsLikedById(position: Int, isLiked: Boolean)

    @Query("SELECT COUNT(*) FROM movie_ids")
    suspend fun getMovieIdsCount(): Int

    @Query("SELECT movie_id FROM movie_ids WHERE id >= :startId ORDER BY id LIMIT :limit")
    suspend fun getMovieIdsByPositionRange(startId: Int, limit: Int): List<Int>
}