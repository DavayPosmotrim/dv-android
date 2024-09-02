package com.davay.android.feature.registration.presentation

import android.text.Editable
import com.davay.android.base.BaseViewModel
import com.davay.android.core.domain.models.UserNameState
import com.davay.android.feature.registration.domain.usecase.RegistrationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class RegistrationViewModel @Inject constructor(
    private val registration: RegistrationUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow(UserNameState.DEFAULT)
    val state: StateFlow<UserNameState>
        get() = _state

    fun buttonClicked(text: Editable?) {
        textCheck(text)
        if (state.value == UserNameState.CORRECT) {
            _state.value = UserNameState.LOADING
            runSafelyUseCase(
                useCaseFlow = registration.execute(text.toString()),
                onSuccess = { _ ->
                    _state.value = UserNameState.SUCCESS
                },
                onFailure = { error ->
                    _state.value = mapErrorToUserNameState(error)
                }
            )
        }
    }

    fun textCheck(text: Editable?) {
        val inputText = text?.toString().orEmpty()
        when {
            inputText.isBlank() -> _state.value = UserNameState.FIELD_EMPTY
            inputText.length < TEXT_LENGTH_MIN -> _state.value = UserNameState.MINIMUM_LETTERS
            inputText.length > TEXT_LENGTH_MAX -> _state.value = UserNameState.MAXIMUM_LETTERS
            inputText.any { !it.isLetter() } -> _state.value = UserNameState.NUMBERS
            else -> _state.value = UserNameState.CORRECT
        }
    }

    companion object {
        private const val TEXT_LENGTH_MIN = 2
        private const val TEXT_LENGTH_MAX = 16
    }
}
