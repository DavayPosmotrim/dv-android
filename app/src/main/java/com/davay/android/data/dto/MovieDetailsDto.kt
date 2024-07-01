package com.davay.android.data.dto

import com.google.gson.annotations.SerializedName

data class MovieDetailsDto(
    val id: Int,
    val name: String,
    val description: String?,
    val year: String?,
    val countries: List<String>?,
    @SerializedName("poster") val imgUrl: String?,
    @SerializedName("alternative_name") val alternativeName: String?,
    @SerializedName("rating_kp") val ratingKinopoisk: Float?,
    @SerializedName("rating_imdb") val ratingImdb: Float?,
    @SerializedName("votes_kp") val numOfMarksKinopoisk: Int?,
    @SerializedName("votes_imdb") val numOfMarksImdb: Int?,
    @SerializedName("movie_length") val duration: Int?,
    val genres: List<GenreDto>,
    val directors: List<String>?,
    val actors: List<String>?
)
