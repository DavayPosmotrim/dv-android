package com.davay.android.feature.changename.presentation

import android.text.Editable
import com.davay.android.base.BaseViewModel
import com.davay.android.domain.models.UserDataFields
import com.davay.android.domain.usecases.GetUserDataUseCase
import com.davay.android.domain.usecases.SetUserDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ChangeNameViewModel @Inject constructor(
    private val setUserData: SetUserDataUseCase,
    private val getUserData: GetUserDataUseCase,
) : BaseViewModel() {

    private val _state = MutableStateFlow(ChangeNameState.DEFAULT)
    val state: StateFlow<ChangeNameState>
        get() = _state

    fun buttonClicked(text: Editable?) {
        textCheck(text)
        if (state.value == ChangeNameState.SUCCESS) {
            setUserData.setUserData(UserDataFields.UserName(text.toString()))
        }
    }

    fun textCheck(text: Editable?) {
        when {
            text.isNullOrBlank() -> _state.value = ChangeNameState.FIELD_EMPTY
            text.length == TEXT_LENGTH_MIN -> _state.value = ChangeNameState.MINIMUM_LETTERS
            text.length > TEXT_LENGTH_MAX -> _state.value = ChangeNameState.MAXIMUM_LETTERS
            text.any { !it.isLetter() } -> _state.value = ChangeNameState.NUMBERS
            else -> _state.value = ChangeNameState.SUCCESS
        }
    }

    fun getNameofUser(): String {
        return getUserData.getUserData(UserDataFields.UserName())
    }

    companion object {
        private const val TEXT_LENGTH_MIN = 1
        private const val TEXT_LENGTH_MAX = 16
    }
}