package com.davay.android.feature.changename.presentation

import android.text.Editable
import androidx.lifecycle.viewModelScope
import com.davay.android.base.BaseViewModel
import com.davay.android.core.domain.models.UserDataFields
import com.davay.android.core.domain.usecases.GetUserDataUseCase
import com.davay.android.feature.changename.domain.usecase.ChangeNameUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChangeNameViewModel @Inject constructor(
    private val getUserData: GetUserDataUseCase,
    private val changeName: ChangeNameUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow(ChangeNameState.DEFAULT)
    private var registrationProcess: Job? = null
    val state: StateFlow<ChangeNameState>
        get() = _state

    fun buttonClicked(text: Editable?) {
        textCheck(text)
        if (state.value == ChangeNameState.CORRECT && text.toString() != getUserName()) {
            _state.value = ChangeNameState.LOADING
            registrationProcess = viewModelScope.launch(Dispatchers.IO) {
                runSafelyUseCase(
                    useCaseFlow = changeName.setUserName(text.toString()),
                    onSuccess = { _ ->
                        _state.value = ChangeNameState.SUCCESS
                    },
                    onFailure = {
                        _state.value = ChangeNameState.NETWORK_ERROR
                    }
                )
            }
        }
    }

    fun cancelRegistration() {
        registrationProcess?.cancel()
    }

    fun textCheck(text: Editable?) {
        when {
            text.isNullOrBlank() -> _state.value = ChangeNameState.FIELD_EMPTY
            text.length == TEXT_LENGTH_MIN -> _state.value = ChangeNameState.MINIMUM_LETTERS
            text.length > TEXT_LENGTH_MAX -> _state.value = ChangeNameState.MAXIMUM_LETTERS
            text.any { !it.isLetter() } -> _state.value = ChangeNameState.NUMBERS
            else -> _state.value = ChangeNameState.CORRECT
        }
    }

    fun getUserName(): String {
        return getUserData.getUserData(UserDataFields.UserName())
    }

    companion object {
        private const val TEXT_LENGTH_MIN = 1
        private const val TEXT_LENGTH_MAX = 16
    }
}