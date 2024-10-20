package com.davay.android.feature.selectmovie.data.network.models

sealed class LikeMovieRequest(
    val movieId: Int,
    val sessionId: String,
    val userId: String
) {
    val path: String = "api/sessions/$sessionId/movies/$movieId/like/"

    class Like(
        movieId: Int,
        sessionId: String,
        userId: String
    ) : LikeMovieRequest(movieId, sessionId, userId)

    class Dislike(
        movieId: Int,
        sessionId: String,
        userId: String
    ) : LikeMovieRequest(movieId, sessionId, userId)
}