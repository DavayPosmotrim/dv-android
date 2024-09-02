package com.davay.android.feature.createsession.presentation.compilations

import android.util.Log
import com.davay.android.BuildConfig
import com.davay.android.base.BaseViewModel
import com.davay.android.core.domain.models.CompilationFilms
import com.davay.android.core.domain.models.ErrorScreenState
import com.davay.android.feature.createsession.domain.model.CompilationSelect
import com.davay.android.feature.createsession.domain.usecase.GetCollectionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class CompilationsViewModel @Inject constructor(
    private val getCollectionsUseCase: GetCollectionsUseCase
) : BaseViewModel() {
    private val _state = MutableStateFlow<CompilationsState>(CompilationsState.Loading)
    val state = _state.asStateFlow()

    private val selectedCompilations = mutableListOf<CompilationSelect>()

    init {
        getCollectionList()
    }

    fun getCollectionList() {
        _state.update { CompilationsState.Loading }
        runSafelyUseCase(
            useCaseFlow = getCollectionsUseCase.execute(),
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

    fun buttonContinueClicked() {
        if (BuildConfig.DEBUG) {
            Log.d("MyTag", selectedCompilations.toString())
        }
    }

    private fun CompilationFilms.toUiModel() = CompilationSelect(
        id = this.id,
        name = this.name,
        cover = this.imgUrl ?: "",
        isSelected = false
    )

    fun hasSelectedCompilations(): Boolean {
        return selectedCompilations.isNotEmpty()
    }

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
}