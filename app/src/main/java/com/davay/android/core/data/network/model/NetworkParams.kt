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

    // WebSocket params
    const val DEVICE_ID_KEY = "Device-ID"
    const val ORIGIN_KEY = "Origin"
    const val ORIGIN_BASE_URL = "http://80.87.108.90/"
    const val BASE_URL = "80.87.108.90:8000/ws/session"
    const val PATH_SESSION_STATUS = "/session_status/"
    const val PATH_USERS = "/users/"
    const val PATH_SESSION_RESULT = "/session_result/"
    const val PATH_ROULETTE = "/roulette/"
    const val PATH_MATCHES = "/matches/"
}