package com.davay.android.core.data.database.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class SessionWithMoviesDb(
    @Embedded val session: SessionEntity,
    @Relation(
        parentColumn = "session_id",
        entityColumn = "movie_id",
        associateBy = Junction(SessionMovieCrossRef::class)
    )
    val movies: List<MovieDetailsEntity>
)
