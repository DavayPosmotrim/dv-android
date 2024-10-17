package com.davay.android.feature.selectmovie.domain

import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.feature.selectmovie.domain.api.SelectMovieRepository
import javax.inject.Inject

class GetMovieDetailsByIdUseCase @Inject constructor(
    private val repository: SelectMovieRepository
) {
    suspend operator fun invoke(movieId: Int): MovieDetails? {
        return repository.getMovieDetailsById(movieId)
    }
}