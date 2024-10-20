package com.davay.android.feature.selectmovie.data.network.models

sealed interface GetMovieRequest {
    val path: String

    class Movie(
        val id: Int
    ) : GetMovieRequest {
        override val path: String = "api/movies/$id/"
    }
}