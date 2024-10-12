package com.davay.android.feature.selectmovie.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.davay.android.BuildConfig
import com.davay.android.base.BaseViewModel
import com.davay.android.core.domain.impl.CommonWebsocketInteractor
import com.davay.android.core.domain.models.ErrorScreenState
import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.core.domain.models.Result
import com.davay.android.feature.selectmovie.domain.FilterDislikedMovieListUseCase
import com.davay.android.feature.selectmovie.domain.GetMovieIdListSizeUseCase
import com.davay.android.feature.selectmovie.domain.GetMovieListUseCase
import com.davay.android.feature.selectmovie.domain.LikeMovieInteractor
import com.davay.android.feature.selectmovie.domain.SwipeMovieUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class SelectMovieViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieListUseCase,
    private val getMovieIdListSizeUseCase: GetMovieIdListSizeUseCase,
    private val filterDislikedMovieListUseCase: FilterDislikedMovieListUseCase,
    private val swipeMovieUseCase: SwipeMovieUseCase,
    private val commonWebsocketInteractor: CommonWebsocketInteractor,
    private val likeMovieInteractor: LikeMovieInteractor
) : BaseViewModel() {
    private val _state = MutableStateFlow<SelectMovieState>(SelectMovieState.Loading)
    val state = _state.asStateFlow()

    private var totalMovieIds = 0
    private var loadedMovies = mutableSetOf<MovieDetails>()

    init {
        initializeMovieList()
        subscribeStates()
    }

    private fun subscribeStates() {
        subscribeSessionResult()
        subscribeSessionStatus()
        subscribeMatches()
    }

    private fun subscribeMatches() {
        viewModelScope.launch(Dispatchers.IO) {
            commonWebsocketInteractor.getMatchesId().collect { result ->
                when (result) {
                    is Result.Success -> {
                        Log.d("SelectMovieViewModel", result.data.toString())
                    }

                    is Result.Error -> {
                        Log.d("SelectMovieViewModel", result.error.toString())
                    }

                    null -> {
                        Log.d("SelectMovieViewModel", null.toString())
                    }
                }
            }
        }
    }

    private fun subscribeSessionResult() {
        viewModelScope.launch(Dispatchers.IO) {
            commonWebsocketInteractor.getSessionResult().collect { result ->
                when (result) {
                    is Result.Success -> {
                        Log.d("SelectMovieViewModel", result.data.toString())
                    }

                    is Result.Error -> {
                        Log.d("SelectMovieViewModel", result.error.toString())
                    }

                    null -> {
                        Log.d("SelectMovieViewModel", null.toString())
                    }
                }
            }
        }
    }

    private fun subscribeSessionStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            commonWebsocketInteractor.getSessionStatus().collect { result ->
                when (result) {
                    is Result.Success -> {
                        Log.d("SelectMovieViewModel", result.data.toString())
                    }

                    is Result.Error -> {
                        Log.d("SelectMovieViewModel", result.error.toString())
                    }

                    null -> {
                        Log.d("SelectMovieViewModel", null.toString())
                    }
                }
            }
        }
    }

    private fun loadMovies(position: Int) {
        runSafelyUseCase(
            useCaseFlow = getMovieDetailsUseCase(position),
            onSuccess = { movieList ->
                if (movieList.isEmpty()) {
                    _state.update {
                        SelectMovieState.Error(ErrorScreenState.MOVIE_LIST_FINISHED)
                    }
                } else {
                    loadedMovies =
                        (state.value as? SelectMovieState.Content)?.movieList ?: mutableSetOf()
                    loadedMovies.addAll(movieList)
                    _state.update {
                        SelectMovieState.Content(movieList = loadedMovies)
                    }
                }
            },
            onFailure = { error ->
                _state.update { SelectMovieState.Error(mapErrorToUiState(error)) }
            }
        )
    }

    private fun initializeMovieList() {
        viewModelScope.launch {
            totalMovieIds = getMovieIdListSizeUseCase()
            loadMovies(0)
        }
    }

    /**
     * Метод вызывает подгрузку фильмов, если это необходимо и устанавливает значение для поля
     * isLike в таблице movieId, данные значения используются для фильтрации элементов и обнолвения
     * списка id элементов, которые потребутются для загрузки данных о фильмах.
     * Вызовы подгрузки фильмов могут быть возможны только при соблюдении неравенства :
     * PRELOAD_SIZE <= PAGINATION_SIZE в SelectMovieRepositoryImpl.
     * Таким образом мы сокращаем количество запросов в сеть, но при этом всегда имеем
     * закэшированные данные фильмов в размере PRELOAD_SIZE, то есть пользователь вообще не значет
     * о процессе подгрузки данных.
     */
    fun onMovieSwiped(position: Int, isLiked: Boolean, onFailureSwipe: () -> Unit = {}) {
        if (position + PRELOAD_SIZE >= loadedMovies.size && loadedMovies.size < totalMovieIds) {
            loadMovies(position)
        }
        viewModelScope.launch {
            runCatching {
                swipeMovieUseCase(position, isLiked)
            }.onSuccess {
                likeMovie(position, isLiked, onFailureSwipe)
            }.onFailure {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "Error on swipe movie, position: $position | ${it.localizedMessage}")
                }
                onFailureSwipe.invoke()
            }
        }

        if (position == totalMovieIds) {
            _state.update { SelectMovieState.ListIsFinished }
        }
    }

    private fun likeMovie(position: Int, isLiked: Boolean, onFailureSwipe: () -> Unit) {
        runSafelyUseCase(
            useCaseFlow = if (isLiked) {
                likeMovieInteractor.likeMovie(position)
            } else {
                likeMovieInteractor.dislikeMovie(position)
            },
            onSuccess = {},
            onFailure = { error ->
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "Error on like movie, position: $position | error -> $error")
                }
                onFailureSwipe.invoke()
            }
        )
    }


    private fun disLikeMovie(position: Int, onFailureSwipe: () -> Unit) {
        runSafelyUseCase(
            useCaseFlow = likeMovieInteractor.dislikeMovie(position),
            onSuccess = {},
            onFailure = { error ->
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "Error on dislike movie, position: $position | error -> $error")
                }
            }
        )
    }

    /**
     * Метод фильтрует список id фильмов по признаку isLiked = false. Метод должен запускаться
     * в случае когда юзер пролистал все фильмы и должен получить список фильмов которые были
     * свайпнуты влево
     */
    fun filterDislikedMovieList() {
        loadedMovies = mutableSetOf()
        _state.update {
            SelectMovieState.Loading
        }

        viewModelScope.launch(Dispatchers.IO) {
            clearAndReset()
        }
    }

    private suspend fun clearAndReset() {
        runCatching {
            filterDislikedMovieListUseCase()
        }.onFailure {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Error on filter disliked movie list ${it.localizedMessage}")
            }
        }
        runCatching {
            initializeMovieList()
        }.onFailure {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Error on initializing movie list ${it.localizedMessage}")
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch(Dispatchers.IO) {
            commonWebsocketInteractor.unsubscribeAllWebSockets()
        }
    }

    private companion object {
        /**
         * Размер подгрузки фильмов, при изменении так же учитывать значение в SelectMovieRepositoryImpl.
         * PRELOAD_SIZE должен быть меньше либо равен PAGINATION_SIZE в SelectMovieRepositoryImpl.
         * PRELOAD_SIZE это колчичество фильмов до конца текущего списка
         */
        const val PRELOAD_SIZE = 5
        val TAG: String = SelectMovieViewModel::class.java.simpleName
    }

}
