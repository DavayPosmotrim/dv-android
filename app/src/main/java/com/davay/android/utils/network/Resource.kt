package com.davay.android.utils.network

import com.davay.android.domain.models.ErrorType

sealed class Resource<out T> {

    data class Success<out T>(val data: T) : Resource<T>()

    data class Error(val errorType: ErrorType) : Resource<Nothing>()
}
