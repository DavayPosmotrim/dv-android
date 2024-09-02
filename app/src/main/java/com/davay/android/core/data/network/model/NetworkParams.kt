package com.davay.android.core.data.network.model

object NetworkParams {
    //    Request params
    const val DEVICE_ID_HEADER = "Device-id"
    const val USER_NAME_BODY_FIELD = "name"

    //    API params
    const val USERS_API_PATH = "api/users/"

    //    Errors
    const val NO_CONNECTION_CODE = -1
    const val BAD_REQUEST_CODE = 400
    const val NOT_FOUND_CODE = 404
    const val SERVER_ERROR_CODE = 500
}