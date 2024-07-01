package com.davay.android.domain.models

data class Session(
    val id: String,
    val users: List<User>,
    val numberOfMatchedMovies: Int?,
    val date: String,
    val status: SessionStatus,
    val imgUrl: String
)
