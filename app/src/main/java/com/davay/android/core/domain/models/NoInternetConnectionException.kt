package com.davay.android.core.domain.models

class NoInternetConnectionException(
    message: String = "No internet connection available"
) : Exception(message)