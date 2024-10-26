package com.davay.android.feature.selectmovie.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.davay.android.BuildConfig
import com.davay.android.base.BaseViewModel
import com.davay.android.core.domain.impl.CommonWebsocketInteractor
import com.davay.android.core.domain.impl.GetMatchesUseCase
import com.davay.android.core.domain.impl.GetSessionDataAndSaveToDbUseCase
import com.davay.android.core.domain.impl.LeaveSessionUseCase
import com.davay.android.core.domain.models.ErrorScreenState
import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.core.domain.models.Result
import com.davay.android.core.domain.models.SessionStatus
import com.davay.android.feature.selectmovie.domain.FilterDislikedMovieListUseCase
import com.davay.android.feature.selectmovie.domain.GetMovieDetailsByIdUseCase
import com.davay.android.feature.selectmovie.domain.GetMovieIdListSizeUseCase
import com.davay.android.feature.selectmovie.domain.GetMovieListUseCase
import com.davay.android.feature.selectmovie.domain.LikeMovieInteractor
import com.davay.android.feature.selectmovie.presentation.models.MovieMatchState
import com.davay.android.feature.selectmovie.presentation.models.SelectMovieState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("TooManyFunctions", "LongParameterList", "LargeClass")
class SelectMovieViewModel @Inject constructor(
    private val getMovieListUseCase: GetMovieListUseCase,
    private val getMovieIdListSizeUseCase: GetMovieIdListSizeUseCase,
    private val filterDislikedMovieListUseCase: FilterDislikedMovieListUseCase,
    private val commonWebsocketInteractor: CommonWebsocketInteractor,
    private val likeMovieInteractor: LikeMovieInteractor,
    private val getMovieDetailsById: GetMovieDetailsByIdUseCase,
    private val leaveSessionUseCase: LeaveSessionUseCase,
    private val getMatchesUseCase: GetMatchesUseCase,
    private val getSessionDataAndSaveToDbUseCase: GetSessionDataAndSaveToDbUseCase
) : BaseViewModel() {
    private val _state = MutableStateFlow<SelectMovieState>(SelectMovieState.Loading)
    val state
        get() = _state.asStateFlow()

    private val _matchState = MutableStateFlow<MovieMatchState>(MovieMatchState.Empty)
    val matchState
        get() = _matchState.asStateFlow()

    private val _stateMatchesCounter = MutableStateFlow(0)
    val stateMatchesCounter
        get() = _stateMatchesCounter.asStateFlow()

    private val _sessionStatusState = MutableStateFlow(SessionStatus.VOTING)
    val sessionStatusState
        get() = _sessionStatusState.asStateFlow()

    private var totalMovieIds = 0
    private var loadedMovies = mutableSetOf<MovieDetails>()

    init {
        initializeMovieList()
        subscribeStates()
        getMatchesCount()
    }

    private fun subscribeStates() {
        subscribeSessionStatus()
        subscribeMatches()
    }

    private fun subscribeMatches() {
        viewModelScope.launch(Dispatchers.IO) {
            commonWebsocketInteractor.getMatchesId().collect { result ->
                when (result) {
                    is Result.Success -> {
                        val movieDetails = getMovieDetailsById(result.data)

                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "subscribeMatches movieDetails = $movieDetails")
                        }

                        _matchState.update {
                            if (movieDetails == null) {
                                MovieMatchState.Empty
                            } else {
                                getMatchesCount()
                                MovieMatchState.Content(movieDetails)
                            }
                        }
                    }

                    is Result.Error -> {
                        _matchState.update {
                            MovieMatchState.Empty
                        }
                    }

                    null -> {
                        _matchState.update {
                            MovieMatchState.Empty
                        }
                    }
                }
            }
        }
    }

    fun emptyMovieMatchState() {
        _matchState.update {
            MovieMatchState.Empty
        }
    }

    fun getMatchesCount() {
        runSafelyUseCase(
            useCaseFlow = getMatchesUseCase(),
            onSuccess = { result ->
                _stateMatchesCounter.update {
                    result.size
                }
            },
            onFailure = { error ->
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "Error on get matches count $error")
                }
            }
        )
    }

    private fun subscribeSessionStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            commonWebsocketInteractor.getSessionStatus().collect { result ->
                when (result) {
                    is Result.Success -> {
                        if (_sessionStatusState.value != SessionStatus.ROULETTE) {
                            _sessionStatusState.update {
                                result.data
                            }
                        }
                    }

                    is Result.Error -> {
                        _sessionStatusState.update {
                            SessionStatus.VOTING
                        }
                    }

                    null -> {
                        _sessionStatusState.update {
                            SessionStatus.VOTING
                        }
                    }
                }
            }
        }
    }

    fun getSessionDataSaveToDbAndLeaveSession() {
        Log.i(TAG, "getSessionDataAndSaveToDb")
        val sessionId = commonWebsocketInteractor.sessionId
        runCatching {
            getSessionDataAndSaveToDbUseCase(sessionId)
        }.onFailure {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "Success on get session data and save to db")
            }
            leaveSessionAndNavigateToHistory()
        }.onSuccess {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "Success on get session data and save to db")
            }
            leaveSessionAndNavigateToHistory()
        }
    }

    private fun loadMovies(position: Int) {
        runSafelyUseCase(
            useCaseFlow = getMovieListUseCase(position),
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
    fun onMovieSwiped(position: Int, isLiked: Boolean) {
        if (position + PRELOAD_SIZE >= loadedMovies.size && loadedMovies.size < totalMovieIds) {
            loadMovies(position)
        }
        likeMovie(position, isLiked)

        if (position == totalMovieIds) {
            _state.update { SelectMovieState.ListIsFinished }
        }
    }

    private fun likeMovie(position: Int, isLiked: Boolean) {
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

    fun leaveSessionAndNavigateToHistory() {
        val action =
            SelectMovieFragmentDirections.actionSelectMovieFragmentToMatchedSessionListFragment()
        disconnect()
        navigate(action)
    }

    private fun disconnect() {
        viewModelScope.launch(Dispatchers.IO) {
            val sessionId = commonWebsocketInteractor.sessionId
            runSafelyUseCase(
                useCaseFlow = leaveSessionUseCase.execute(sessionId),
                onSuccess = {},
                onFailure = { error ->
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "Error on leave session $sessionId, error -> $error")
                    }
                }
            )
            commonWebsocketInteractor.unsubscribeWebsockets()
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
