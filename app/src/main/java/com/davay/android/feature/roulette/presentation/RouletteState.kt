package com.davay.android.feature.roulette.presentation

import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.feature.roulette.presentation.model.UserRouletteModel

sealed interface RouletteState {
    class Init(
        val users: List<UserRouletteModel>,
        val films: List<MovieDetails>
    ) : RouletteState

    class Waiting(
        val users: List<UserRouletteModel>,
        val films: List<MovieDetails>
    ) : RouletteState

    class Roulette(
        val index: Int,
        val count: Int,
        val users: List<UserRouletteModel>,
        val films: List<MovieDetails>
    ) : RouletteState

    data class Match constructor(
        val film: MovieDetails,
    ) : RouletteState

    object Error : RouletteState
}
