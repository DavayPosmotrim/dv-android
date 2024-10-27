package com.davay.android.core.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.davay.android.core.data.database.entity.MovieDetailsEntity
import com.davay.android.core.data.database.entity.SessionEntity
import com.davay.android.core.data.database.entity.SessionMovieCrossRef
import com.davay.android.core.data.database.entity.SessionWithMoviesDb
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    // Получение всех сохраненных сессий
    @Query("SELECT * FROM sessions")
    fun getSessions(): Flow<List<SessionEntity>>

    // Получение по sessionId сессии с её списком фильмов.
    @Transaction
    @Query("SELECT * FROM sessions WHERE session_id = :sessionId")
    suspend fun getSessionWithMovies(sessionId: String): SessionWithMoviesDb

    // Сохранение сессии (без фильмов)
    @Upsert
    suspend fun insertSession(session: SessionEntity)

    // Сохранение фильма
    @Upsert
    suspend fun insertMovie(movie: MovieDetailsEntity)

    @Query("SELECT * FROM movies where movie_id = :id")
    suspend fun getMovieDetailsById(id: Int): MovieDetailsEntity?

    // Сохранение связи сессии и фильма с помощью sessionId и movieId
    @Upsert
    suspend fun insertSessionMovieReference(sessionMovieCrossRef: SessionMovieCrossRef)

    // Транзакция сохранения сессии с фильмами
    @Transaction
    suspend fun saveSessionWithFilms(
        session: SessionEntity,
        movies: List<MovieDetailsEntity>
    ) {
        insertSession(session)
        movies.forEach {
            insertMovie(it)
            insertSessionMovieReference(
                SessionMovieCrossRef(
                    sessionId = session.sessionId,
                    movieId = it.movieId,
                )
            )
        }
    }
}
