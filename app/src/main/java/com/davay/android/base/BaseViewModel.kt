package com.davay.android.base

import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.commit451.translationviewdraghelper.BuildConfig
import com.davay.android.R
import com.davay.android.core.domain.models.ErrorScreenState
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import com.davay.android.utils.DEFAULT_DELAY_600
import com.davay.android.utils.debounceUnitFun
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    private val _navigation = MutableLiveData<Event<NavigationCommand>>()
    val navigation: LiveData<Event<NavigationCommand>> get() = _navigation

    private val debounceNavigate = debounceUnitFun<NavigationCommand>(
        coroutineScope = viewModelScope,
        delayMillis = DEFAULT_DELAY_600,
        useLastParam = false
    )

    fun navigate(@IdRes navDirections: Int) {
        debounceNavigate(NavigationCommand.ToDirection(navDirections)) { command ->
            _navigation.value = Event(command)
        }
    }

    fun navigate(@IdRes navDirections: Int, bundle: Bundle) {
        debounceNavigate(NavigationCommand.ToDirection(navDirections, bundle)) { command ->
            _navigation.value = Event(command)
        }
    }

    fun navigate(@IdRes navDirections: Int, navOptions: NavOptions) {
        debounceNavigate(NavigationCommand.ToDirection(navDirections, null, navOptions)) { command ->
            _navigation.value = Event(command)
        }
    }

    fun navigate(@IdRes navDirections: Int, bundle: Bundle, navOptions: NavOptions) {
        debounceNavigate(NavigationCommand.ToDirection(navDirections, bundle, navOptions)) { command ->
            _navigation.value = Event(command)
        }
    }

    fun navigateBack() {
        debounceNavigate(NavigationCommand.Back) { command ->
            _navigation.value = Event(command)
        }
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