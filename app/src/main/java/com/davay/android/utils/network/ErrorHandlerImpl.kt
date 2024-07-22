package com.davay.android.utils.network

import com.davay.android.data.network.ApiConstants
import com.davay.android.domain.models.ErrorType
import retrofit2.HttpException

class ErrorHandlerImpl : ErrorHandler {
    override fun handleErrorCode(resultCode: Int) = Resource.Error(
        when (resultCode) {
            ApiConstants.BAD_REQUEST -> ErrorType.BAD_REQUEST
            ApiConstants.NOT_FOUND -> ErrorType.NOT_FOUND
            else -> ErrorType.UNEXPECTED
        }
    )

    override fun handleHttpException(exception: HttpException) = handleErrorCode(exception.code())
}
