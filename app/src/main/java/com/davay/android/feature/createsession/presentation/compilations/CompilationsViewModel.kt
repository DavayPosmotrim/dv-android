package com.davay.android.feature.createsession.presentation.compilations

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.davay.android.BuildConfig
import com.davay.android.core.domain.impl.CommonWebsocketInteractor
import com.davay.android.core.domain.models.CompilationFilms
import com.davay.android.core.domain.models.ErrorScreenState
import com.davay.android.core.domain.models.converter.toSessionShort
import com.davay.android.feature.createsession.domain.model.CompilationSelect
import com.davay.android.feature.createsession.domain.model.SessionType
import com.davay.android.feature.createsession.domain.usecase.CreateSessionUseCase
import com.davay.android.feature.createsession.domain.usecase.GetCollectionsUseCase
import com.davay.android.feature.createsession.presentation.createsession.CreateSessionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class CompilationsViewModel @Inject constructor(
    private val getCollectionsUseCase: GetCollectionsUseCase,
    private val createSessionUseCase: CreateSessionUseCase,
    commonWebsocketInteractor: CommonWebsocketInteractor
) : CreateSessionViewModel(commonWebsocketInteractor) {
    private val _state = MutableStateFlow<CompilationsState>(CompilationsState.Loading)
    val compilationsState = _state.asStateFlow()

    private val selectedCompilations = mutableListOf<CompilationSelect>()

    init {
        getCollectionList()
    }

    fun getCollectionList() {
        runSafelyUseCase(
            useCaseFlow = getCollectionsUseCase(),
            onSuccess = { collections ->
                if (collections.isEmpty()) {
                    _state.update { CompilationsState.Error(ErrorScreenState.EMPTY) }
                } else {
                    val compilations = collections.map { it.toUiModel() }
                    _state.update { CompilationsState.Content(compilations) }
                }
            },
            onFailure = { error ->
                _state.update { CompilationsState.Error(mapErrorToUiState(error)) }
            }
        )
    }

    fun compilationClicked(compilation: CompilationSelect) {
        if (compilation.isSelected) {
            selectedCompilations.add(compilation)
        } else {
            selectedCompilations.remove(compilation)
        }
    }

    private fun CompilationFilms.toUiModel() = CompilationSelect(
        id = this.id,
        name = this.name,
        cover = this.imgUrl ?: "",
        isSelected = false
    )

    fun resetSelections() {
        selectedCompilations.clear()
        _state.update { currentState ->
            if (currentState is CompilationsState.Content) {
                CompilationsState.Content(currentState.compilationList.map { it.copy(isSelected = false) })
            } else {
                currentState
            }
        }
    }

    /**
     * Метод проверяет на пустоту список выбранных коллекций, если список пустой, вызывает баннер.
     * Для не пустого списка вызывает создание сессии.
     * Навигация при этом вызывается только после успешного возврата.
     * Данные сессии передаем через bundle в navigateToWaitSession.
     */
    fun createSessionAndNavigateToWaitSessionScreen(showBanner: () -> Unit) {
        if (selectedCompilations.isEmpty()) {
            showBanner.invoke()
        } else {
            viewModelScope.launch {
                val collections = selectedCompilations.map {
                    it.id
                }
                _state.update {
                    CompilationsState.CreateSessionLoading
                }
                runSafelyUseCase(
                    useCaseFlow = createSessionUseCase(SessionType.COLLECTIONS, collections),
                    onSuccess = { session ->
                        if (BuildConfig.DEBUG) {
                            Log.v(TAG, "session = $session")
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
                            handledError = ErrorScreenState.ERROR_BUILD_SESSION_COLLECTIONS
                        }
                        _state.update { CompilationsState.Error(handledError) }
                    }
                )
            }
        }
    }

    private companion object {
        val TAG = CompilationsViewModel::class.simpleName
    }
}