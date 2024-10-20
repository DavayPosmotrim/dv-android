package com.davay.android.feature.sessionlist.data.network

sealed class ConnectToSessionRequest(val path: String) {
    class ConnectToSession(val sessionId: String, val userId: String) :
        ConnectToSessionRequest(path = "api/sessions/$sessionId/connection/")

    class Session(val sessionId: String, val userId: String) :
        ConnectToSessionRequest(path = "api/sessions/$sessionId/")
}