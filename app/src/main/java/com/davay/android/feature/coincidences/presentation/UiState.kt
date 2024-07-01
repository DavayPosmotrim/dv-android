package com.davay.android.feature.coincidences.presentation

import com.davay.android.domain.models.ErrorType
import com.davay.android.feature.selectmovie.domain.models.MovieDetailsDemo

sealed class UiState {

    data object Empty : UiState()
    data object Loading : UiState()
    data class Data(val data: List<MovieDetailsDemo> = emptyList()) : UiState()
    data class Error(val errorType: ErrorType) : UiState()
}