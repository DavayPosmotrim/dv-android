package com.davay.android.feature.roulette.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StartRouletteMessageDto(
    @SerialName("random_movie_id") val randomMovieId: Int
)