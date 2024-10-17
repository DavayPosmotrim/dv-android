package com.davay.android.feature.selectmovie.presentation.models

import com.davay.android.core.domain.models.MovieDetails

sealed interface MovieMatchState {
    data object Empty : MovieMatchState
    class Content(val movieDetails: MovieDetails) : MovieMatchState
}