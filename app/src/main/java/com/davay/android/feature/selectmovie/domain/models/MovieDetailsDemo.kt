package com.davay.android.feature.selectmovie.domain.models

// используется для demo
data class MovieDetailsDemo(
    val kinopoiskId: Int? = null,
    val name: String,
    val description: String? = null,
    val year: Int? = null,
    val countries: List<String> = emptyList(),
    val posterUrl: String? = null,
    val alternativeName: String? = null,
    val ratingKinopoisk: Float? = null,
    val ratingImdb: Float? = null,
    val movieLengthMin: Int? = null,
    val genres: List<String> = emptyList(),
    val topCast: List<String> = emptyList(),
    val directors: List<String> = emptyList(),
    val shortDescription: String? = null,
    val votesKinopoisk: Int? = null,
    val votesImdb: Int? = null,
    val isLiked: Boolean = false
)