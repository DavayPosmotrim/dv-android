package com.davay.android.feature.selectmovie.data.network.models

import com.davay.android.core.data.dto.MovieDetailsDto

sealed interface GetMovieResponse {
    class Movie(val value: MovieDetailsDto) : GetMovieResponse
}