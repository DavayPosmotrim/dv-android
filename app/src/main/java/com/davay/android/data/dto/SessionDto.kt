package com.davay.android.data.dto

import com.google.gson.annotations.SerializedName

data class SessionDto(
    val id: String,
    val users: List<UserDto>,
    @SerializedName("matched_movies") val numberOfMatchedMovies: Int?,
    val date: String,
    val status: SessionStatusDto,
    @SerializedName("session_img") val imgUrl: String
)