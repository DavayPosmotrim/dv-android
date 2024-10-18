package com.davay.android.feature.selectmovie.domain

import com.davay.android.core.domain.impl.CommonWebsocketInteractor
import com.davay.android.feature.selectmovie.domain.api.LikeMovieRepository
import javax.inject.Inject

class LikeMovieInteractor @Inject constructor(
    commonWebsocketInteractor: CommonWebsocketInteractor,
    private val repository: LikeMovieRepository
) {
    private val sessionId = commonWebsocketInteractor.sessionId

    fun likeMovie(position: Int) = repository.likeMovie(position, sessionId)

    fun dislikeMovie(position: Int) = repository.dislikeMovie(position, sessionId)
}