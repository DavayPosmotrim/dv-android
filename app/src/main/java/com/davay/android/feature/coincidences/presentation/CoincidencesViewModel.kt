package com.davay.android.feature.coincidences.presentation

import androidx.lifecycle.viewModelScope
import com.davay.android.base.BaseViewModel
import com.davay.android.base.usecases.GetData
import com.davay.android.feature.coincidences.ErrorType
import com.davay.android.feature.coincidences.di.GET_TEST_MOVIE_USE_CASE
import com.davay.android.feature.selectmovie.domain.models.MovieDetailsDemo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class CoincidencesViewModel @Inject constructor(
    @Named(GET_TEST_MOVIE_USE_CASE)
    private val getData: GetData<MovieDetailsDemo, ErrorType>
) : BaseViewModel() {

    private val _state: MutableStateFlow<UiState> = MutableStateFlow(UiState.Empty)
    val state = _state.asStateFlow()

    init {
        getCoincidences()
    }

    fun getCoincidences() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.emit(UiState.Loading)

            getData.getData().fold(
                onSuccess = { movies ->
                    _state.value =
                        if (movies.isEmpty()) UiState.Empty else UiState.Data(data = movies)
                },
                onError = { errorType ->
                    _state.value = UiState.Error(errorType)
                }
            )
        }
    }

    @Suppress("Detekt.FunctionOnlyReturningConstant")
    fun isHintShown(): Boolean = false // Запровайдить префы
}