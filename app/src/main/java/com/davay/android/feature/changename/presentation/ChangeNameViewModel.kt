package com.davay.android.feature.changename.presentation

import android.text.Editable
import androidx.lifecycle.viewModelScope
import com.davay.android.base.BaseViewModel
import com.davay.android.core.domain.models.UserDataFields
import com.davay.android.core.domain.models.UserNameState
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

    private val _state = MutableStateFlow(UserNameState.DEFAULT)
    private var registrationProcess: Job? = null
    val state: StateFlow<UserNameState>
        get() = _state

    fun buttonClicked(text: Editable?) {
        textCheck(text)
        if (state.value == UserNameState.CORRECT && text.toString() != getUserName()) {
            _state.value = UserNameState.LOADING
            registrationProcess = viewModelScope.launch(Dispatchers.IO) {
                runSafelyUseCase(
                    useCaseFlow = changeName.setUserName(text.toString()),
                    onSuccess = { _ ->
                        _state.value = UserNameState.SUCCESS
                    },
                    onFailure = { error ->
                        _state.value = mapErrorToUserNameState(error)
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
            text.isNullOrBlank() -> _state.value = UserNameState.FIELD_EMPTY
            text.length == TEXT_LENGTH_MIN -> _state.value = UserNameState.MINIMUM_LETTERS
            text.length > TEXT_LENGTH_MAX -> _state.value = UserNameState.MAXIMUM_LETTERS
            text.any { !it.isLetter() } -> _state.value = UserNameState.NUMBERS
            else -> _state.value = UserNameState.CORRECT
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