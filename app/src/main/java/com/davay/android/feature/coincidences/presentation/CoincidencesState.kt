package com.davay.android.feature.coincidences.presentation

import com.davay.android.core.domain.models.ErrorScreenState
import com.davay.android.core.domain.models.MovieDetails

sealed class CoincidencesState {
    data object Loading : CoincidencesState()
    class Content(val data: List<MovieDetails> = emptyList()) : CoincidencesState()
    class Error(val errorType: ErrorScreenState) : CoincidencesState()
}