package com.davay.android.feature.roulette.presentation

import androidx.lifecycle.viewModelScope
import com.davay.android.base.BaseViewModel
import com.davay.android.feature.roulette.presentation.model.UserRouletteModel
import com.davay.android.feature.selectmovie.domain.models.MovieDetailsDemo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

class RouletteViewModel @Inject constructor() : BaseViewModel() {

    private val _state: MutableStateFlow<RouletteState>
    val state: StateFlow<RouletteState>
        get() = _state

    private var users: List<UserRouletteModel>
    private val films: List<MovieDetailsDemo>
    private val watchFilmId: Int

    init {
        users = listOf(
            UserRouletteModel(1, "Masha"),
            UserRouletteModel(2, "Sasha"),
            UserRouletteModel(TEMP_NUMBER_3, "Sasha Sasha Sasha Sasha Sasha")
        )
        films = listOf(
            MovieDetailsDemo(
                kinopoiskId = 1,
                movieName = "Название 1",
                ratingKinopoisk = 2.0f,
                englishName = "gfdавgd",
                year = 2008,
                posterUrl = "https://clck.ru/3BACfT",
                movieLengthMin = 90,
                countries = listOf("USA", "UK")
            ),
            MovieDetailsDemo(
                kinopoiskId = 2,
                movieName = "Название 2",
                ratingKinopoisk = 7.0f,
                englishName = "gfdgdпв",
                year = 2008,
                posterUrl = "https://ir.ozone.ru/s3/multimedia-j/c1000/6379140283.jpg",
                movieLengthMin = 90,
                countries = listOf("USA", "UK")
            ),
            MovieDetailsDemo(
                kinopoiskId = 3,
                movieName = "Название 3",
                ratingKinopoisk = 9.4f,
                englishName = "gfdgd",
                year = 1991,
                posterUrl = "https://static.insales-cdn.com/images/products/1/2985/388467625/%D0%BF0362.png",
                movieLengthMin = 90,
                countries = listOf("USA", "UK")
            ),
            MovieDetailsDemo(
                kinopoiskId = 4,
                movieName = "Название 4",
                ratingKinopoisk = 5.0f,
                englishName = "gfавпdgd",
                year = 2018,
                posterUrl = "https://clck.ru/3BACbb",
                movieLengthMin = 90,
                countries = listOf("UK")
            ),
            MovieDetailsDemo(
                kinopoiskId = 5,
                movieName = "Название 5",
                ratingKinopoisk = 8.9f,
                englishName = "hjjgjkhk hjjh jhjhjk hjkjk",
                year = 2010,
                posterUrl = "https://clck.ru/3BACae",
                movieLengthMin = 90,
                countries = listOf("USA")
            ),
        )

        _state = MutableStateFlow(RouletteState.Init(users, films))
        watchFilmId = Random.nextInt(1, TEMP_NUMBER_6)
        if (films.any { it.kinopoiskId == watchFilmId }) {
            checkUsers()
        } else {
            _state.value = RouletteState.Error
        }
    }

    private fun checkUsers() {
        viewModelScope.launch {
            tempFun().collect { list ->
                if (list.isNullOrEmpty()) {
                    _state.value = RouletteState.Error
                } else {
                    users = list
                    _state.value = RouletteState.Waiting(users = users, films = films)
                    if (users.all { it.isConnected }) {
                        delay(DELAY_TIME_MS_1000)
                        val index = films.indexOfFirst { it.kinopoiskId == watchFilmId }
                        _state.value = RouletteState.Roulette(
                            index = index,
                            count = films.size,
                            users = users,
                            films = films
                        )
                    }
                }
            }
        }
    }

    fun rouletteScrollingStopped() {
        viewModelScope.launch {
            delay(DELAY_TIME_MS_1000)
            _state.value =
                films.find { it.kinopoiskId == watchFilmId }?.let { RouletteState.Match(it) }
                    ?: RouletteState.Error
        }
    }

    private fun tempFun(): Flow<List<UserRouletteModel>?> = flow {
        for (i in 0..2) {
            delay(DELAY_TIME_MS_1000 * TEMP_NUMBER_6)
            val list = mutableListOf<UserRouletteModel>()
            list.addAll(users.map { it.copy() })
            list[i].isConnected = true
            emit(list)
        }
    }

    fun rouletteStart() {
        // отправляем запрос на добавление других пользователей в рулетку
    }

    companion object {
        private const val DELAY_TIME_MS_1000 = 1000L
        private const val TEMP_NUMBER_6 = 6
        private const val TEMP_NUMBER_3 = 3
    }
}
