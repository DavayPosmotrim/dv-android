package com.davay.android.feature.createsession.presentation.genre

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.davay.android.BuildConfig
import com.davay.android.core.domain.impl.CommonWebsocketInteractor
import com.davay.android.core.domain.models.ErrorScreenState
import com.davay.android.core.domain.models.Genre
import com.davay.android.core.domain.models.converter.toSessionShort
import com.davay.android.feature.createsession.domain.model.GenreSelect
import com.davay.android.feature.createsession.domain.model.SessionType
import com.davay.android.feature.createsession.domain.usecase.CreateSessionUseCase
import com.davay.android.feature.createsession.domain.usecase.GetGenresUseCase
import com.davay.android.feature.createsession.presentation.createsession.CreateSessionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GenreViewModel @Inject constructor(
    private val getGenresUseCase: GetGenresUseCase,
    private val createSessionUseCase: CreateSessionUseCase,
    commonWebsocketInteractor: CommonWebsocketInteractor
) : CreateSessionViewModel(commonWebsocketInteractor) {
    private val _state = MutableStateFlow<GenreState>(GenreState.Loading)
    val genreState = _state.asStateFlow()

    private val selectedGenre = mutableListOf<GenreSelect>()

    init {
        getGenreList()
    }

    fun getGenreList() {
        _state.update { GenreState.Loading }
        runSafelyUseCase(
            useCaseFlow = getGenresUseCase(),
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

    private fun Genre.toUiModel() = GenreSelect(
        name = this.name,
        isSelected = false
    )

    /**
     * Метод проверяет на пустоту список выбранных коллекций, если список пустой, вызывает баннер.
     * Для не пустого списка вызывает создание сессии.
     * Навигация при этом вызывается только после успешного возврата.
     * Данные сессии передаем через bundle в navigateToWaitSession.
     */
    fun createSessionAndNavigateToWaitSessionScreen(showBanner: () -> Unit) {
        if (selectedGenre.isEmpty()) {
            showBanner.invoke()
        } else {
            viewModelScope.launch {
                val genreList = selectedGenre.map {
                    it.name
                }
                _state.update {
                    GenreState.CreateSessionLoading
                }
                runSafelyUseCase(
                    useCaseFlow = createSessionUseCase(SessionType.GENRES, genreList),
                    onSuccess = { session ->
                        if (BuildConfig.DEBUG) {
                            Log.v(TAG, "error -> $session")
                        }
                        subscribeToWebsocketsAndUpdateSessionId(sessionId = session.id).also {
                            viewModelScope.launch(Dispatchers.Main) {
                                navigateToWaitSession(session.toSessionShort())
                            }
                        }
                    },
                    onFailure = { error ->
                        if (BuildConfig.DEBUG) {
                            Log.v(TAG, "error -> $error")
                        }
                        var handledError = mapErrorToUiState(error)
                        if (handledError == ErrorScreenState.SERVER_ERROR) {
                            handledError = ErrorScreenState.ERROR_BUILD_SESSION_GENRES
                        }
                        _state.update { GenreState.Error(handledError) }
                    }
                )
            }
        }
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

    private companion object {
        val TAG = GenreViewModel::class.simpleName
    }
}
