package com.davay.android.feature.createsession.presentation.genre

import android.util.Log
import com.davay.android.BuildConfig
import com.davay.android.base.BaseViewModel
import com.davay.android.core.domain.models.ErrorScreenState
import com.davay.android.core.domain.models.Genre
import com.davay.android.feature.createsession.domain.model.GenreSelect
import com.davay.android.feature.createsession.domain.usecase.GetGenresUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class GenreViewModel @Inject constructor(
    private val getGenresUseCase: GetGenresUseCase
) : BaseViewModel() {
    private val _state = MutableStateFlow<GenreState>(GenreState.Loading)
    val state = _state.asStateFlow()

    private val selectedGenre = mutableListOf<GenreSelect>()

    init {
        getGenreList()
    }

    fun getGenreList() {
        _state.update { GenreState.Loading }
        runSafelyUseCase(
            useCaseFlow = getGenresUseCase.execute(),
            onSuccess = { genres ->
                if (genres.isEmpty()) {
                    _state.update { GenreState.Error(ErrorScreenState.EMPTY) }
                } else {
                    val uiGenres = genres.map { it.toUiModel() }
                    _state.update { GenreState.Content(uiGenres) }
                }
            },
            onFailure = { error ->
                _state.update { GenreState.Error(mapErrorToUiState(error)) }
            }
        )
    }

    fun genreClicked(genre: GenreSelect) {
        if (genre.isSelected) {
            selectedGenre.add(genre)
        } else {
            selectedGenre.remove(genre)
        }
    }

    fun buttonContinueClicked() {
        if (BuildConfig.DEBUG) {
            Log.d("MyTagGenre", selectedGenre.toString())
        }
    }

    private fun Genre.toUiModel() = GenreSelect(
        name = this.name,
        isSelected = false
    )

    // Метод для проверки, выбран ли хотя бы один жанр
    fun hasSelectedGenres(): Boolean {
        return selectedGenre.isNotEmpty()
    }

    fun resetSelections() {
        selectedGenre.clear()
        _state.update { currentState ->
            if (currentState is GenreState.Content) {
                val resetGenres = currentState.genreList.map { it.copy(isSelected = false) }
                GenreState.Content(resetGenres)
            } else {
                currentState
            }
        }
    }
}
