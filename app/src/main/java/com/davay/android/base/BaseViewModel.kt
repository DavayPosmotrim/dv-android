package com.davay.android.base

import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.davay.android.BuildConfig
import com.davay.android.R
import com.davay.android.core.domain.models.ErrorScreenState
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import com.davay.android.core.domain.models.UserNameState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    private val _navigation = MutableLiveData<Event<NavigationCommand>>()
    val navigation: LiveData<Event<NavigationCommand>> get() = _navigation

    fun navigate(@IdRes navDirections: Int) {
        _navigation.value = Event(NavigationCommand.ToDirection(navDirections))
    }

    fun navigate(@IdRes navDirections: Int, bundle: Bundle) {
        _navigation.value = Event(NavigationCommand.ToDirection(navDirections, bundle))
    }

    fun navigate(@IdRes navDirections: Int, navOptions: NavOptions) {
        _navigation.value = Event(NavigationCommand.ToDirection(navDirections, null, navOptions))
    }

    fun navigate(@IdRes navDirections: Int, bundle: Bundle, navOptions: NavOptions) {
        _navigation.value = Event(NavigationCommand.ToDirection(navDirections, bundle, navOptions))
    }

    fun navigateBack() {
        _navigation.value = Event(NavigationCommand.Back)
    }

    /**
     * Метод чистит backstack до MainFragment и делает сооветствующий переход
     */
    fun clearBackStackToMain() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.main_navigation_graph, inclusive = true)
            .build()

        navigate(R.id.mainFragment, navOptions)
    }

    fun clearBackStackToMainAndNavigate(@IdRes navDirections: Int) {
        clearBackStackToMain()
        navigate(navDirections)
    }

    protected fun mapErrorToUiState(errorType: ErrorType): ErrorScreenState {
        return when (errorType) {
            ErrorType.NO_CONNECTION -> ErrorScreenState.NO_INTERNET
            ErrorType.NOT_FOUND -> ErrorScreenState.SERVER_ERROR
            ErrorType.BAD_REQUEST -> ErrorScreenState.SERVER_ERROR
            ErrorType.APP_VERSION_ERROR -> ErrorScreenState.APP_VERSION_ERROR
            else -> ErrorScreenState.SERVER_ERROR
        }
    }

    protected fun mapErrorToUserNameState(errorType: ErrorType): UserNameState {
        return when (errorType) {
            ErrorType.NO_CONNECTION -> UserNameState.NO_CONNECTION
            ErrorType.APP_VERSION_ERROR -> UserNameState.APP_VERSION_ERROR
            else -> UserNameState.SERVER_ERROR
        }
    }

    protected inline fun <reified D> runSafelyUseCase(
        useCaseFlow: Flow<Result<D, ErrorType>>,
        noinline onFailure: ((ErrorType) -> Unit)? = null,
        crossinline onSuccess: (D) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                useCaseFlow.collect { result ->
                    when (result) {
                        is Result.Success -> onSuccess(result.data)
                        is Result.Error -> {
                            onFailure?.invoke(result.error)
                        }
                    }
                }
            }.onFailure { error ->
                if (BuildConfig.DEBUG) {
                    Log.v(BASE_VM_TAG, "error -> ${error.localizedMessage}")
                    error.printStackTrace()
                }
                onFailure?.invoke(ErrorType.UNKNOWN_ERROR)
            }
        }
    }

    companion object {
        val BASE_VM_TAG = BaseViewModel::class.simpleName
    }
}