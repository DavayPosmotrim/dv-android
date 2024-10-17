package com.davay.android.feature.selectmovie.presentation.models

import com.davay.android.core.domain.models.ErrorScreenState
import com.davay.android.core.domain.models.MovieDetails

sealed interface SelectMovieState {
    data object Loading : SelectMovieState
    class Error(val errorType: ErrorScreenState) : SelectMovieState
    class Content(val movieList: MutableSet<MovieDetails>) : SelectMovieState
    data object ListIsFinished : SelectMovieState
}