package com.davay.android.core.data.network.model.getsession

class GetSessionRequest(val sessionId: String, val userId: String) {
    val path: String = "api/sessions/$sessionId/"
}