package com.davay.android.domain.models

data class MovieDetails(
    val id: Int,
    val name: String,
    val description: String?,
    val year: String?,
    val countries: List<String>?,
    val imgUrl: String?,
    val alternativeName: String?,
    val ratingKinopoisk: Float?,
    val ratingImdb: Float?,
    val numOfMarksKinopoisk: Int?,
    val numOfMarksImdb: Int?,
    val duration: Int?,
    val genres: List<String>,
    val directors: List<String>?,
    val actors: List<String>?
)
