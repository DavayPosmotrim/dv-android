package com.davay.android.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieDetailsEntity(
    @PrimaryKey val movieId: Int,
    val name: String,
    val description: String? = null,
    val year: String? = null,
    val countries: String? = null,
    val imgUrl: String? = null,
    val alternativeName: String? = null,
    val ratingKinopoisk: Float? = null,
    val ratingImdb: Float? = null,
    val numOfMarksKinopoisk: Int? = null,
    val numOfMarksImdb: Int? = null,
    val duration: Int? = null,
    val genres: String,
    val directors: String? = null,
    val actors: String? = null,
)
