package com.davay.android.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionResultDto(
    @SerialName("id") val id: String,
    @SerialName("users") val users: List<UserNameDto>,
    @SerialName("matched_movies") val matchedMovies: List<MovieDto>,
    @SerialName("date") val date: String,
    @SerialName("image") val imgUrl: String,
    @SerialName("matched_movies_count") val matchedMoviesCount: Int,
)
