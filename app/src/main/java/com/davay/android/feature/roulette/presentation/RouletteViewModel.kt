package com.davay.android.feature.roulette.presentation

import androidx.lifecycle.viewModelScope
import com.davay.android.base.BaseViewModel
import com.davay.android.core.domain.impl.CommonWebsocketInteractor
import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.feature.roulette.domain.impl.RouletteMoviesUseCase
import com.davay.android.feature.roulette.domain.impl.StartRouletteUseCase
import com.davay.android.feature.roulette.presentation.model.UserRouletteModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class RouletteViewModel @Inject constructor(
    private val commonWebsocketInteractor: CommonWebsocketInteractor,
    private val startRouletteUseCase: StartRouletteUseCase,
    private val rouletteMoviesUseCase: RouletteMoviesUseCase,
) : BaseViewModel() {

    private val _state: MutableStateFlow<RouletteState> = MutableStateFlow(
        RouletteState.Loading
    )
    val state: StateFlow<RouletteState>
        get() = _state

    private var users: List<UserRouletteModel> = emptyList()
    private var films: List<MovieDetails> = emptyList()
    private var watchFilmId: Int = -1
    private val sessionId: String = commonWebsocketInteractor.sessionId.toString()

    init {
        subscribeToWs()
    }

    private fun subscribeToWs() {
        subscribeSessionResult()
        subscribeRouletteId()
    }

    private fun subscribeSessionResult() {
        runSafelyUseCaseWithNullResponse(
            useCaseFlow = commonWebsocketInteractor.getSessionResult(),
            onSuccess = { session ->
                if (session != null) {
                    runSafelyUseCase(
                        useCaseFlow = rouletteMoviesUseCase.getMoviesByIdList(idList = session.matchedMovieIdList),
                        onSuccess = { movies ->
                            val userList =
                                session.users.mapIndexed { index, user ->
                                    UserRouletteModel(
                                        id = index,
                                        name = user,
                                        isConnected = false
                                    )
                                }
                            users = userList
                            films = movies
                            _state.update {
                                RouletteState.Init(
                                    users = users,
                                    films = films,
                                    watchFilmId = watchFilmId
                                )
                            }

                        },
                        onFailure = {
                            _state.update { RouletteState.Error }
                        }
                    )
                }
            },
            onFailure = {
                _state.update { RouletteState.Error }
            }
        )
    }

    private fun subscribeRouletteId() {
        runSafelyUseCaseWithNullResponse(
            useCaseFlow = commonWebsocketInteractor.getRouletteId(),
            onSuccess = { filmId ->
                if (filmId != null) {
                    watchFilmId = filmId
                    _state.update {
                        RouletteState.Init(
                            users = users,
                            films = films,
                            watchFilmId = watchFilmId
                        )
                    }
                }
            },
            onFailure = {
                _state.update { RouletteState.Error }
            }
        )
    }

    fun autoScrollingStarted() {
        viewModelScope.launch {
            closeSockets()
            for (i in users.indices) {
                delay(DELAY_TIME_MS_1000)
                users[i].isConnected = true
                _state.update {
                    RouletteState.Waiting(
                        users = users,
                        films = films,
                        watchFilmId = watchFilmId
                    )
                }
            }
            delay(DELAY_TIME_MS_1000)
            val index = films.indexOfFirst { it.id == watchFilmId }
            _state.value = RouletteState.Roulette(
                index = index,
                count = films.size,
                users = users,
                films = films,
            )
        }
    }

    private fun closeSockets() {
        viewModelScope.launch {
            commonWebsocketInteractor.unsubscribeWebsockets()
        }
    }

    fun rouletteScrollingStopped() {
        viewModelScope.launch {
            delay(DELAY_TIME_MS_1000)
            _state.value =
                films.find { it.id == watchFilmId }?.let { RouletteState.Match(it) }
                    ?: RouletteState.Error
        }
    }

    fun rouletteStart() {
        runSafelyUseCase(
            useCaseFlow = startRouletteUseCase.startRoulette(sessionId),
            onSuccess = {},
            onFailure = { _state.update { RouletteState.Error } }
        )
    }

    fun navigateToMainFragment() {
        navigate(RouletteFragmentDirections.actionRouletteFragmentToMainFragment())
    }

    fun navigateToSessionHistory() {
        navigate(RouletteFragmentDirections.actionRouletteFragmentToMatchedSessionListFragment())
    }

    companion object {
        private const val DELAY_TIME_MS_1000 = 1000L
    }
}
