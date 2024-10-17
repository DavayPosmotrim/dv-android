package com.davay.android.feature.coincidences.data.network.models

class GetSessionRequest(val sessionId: String, val userId: String) {
    val path: String = "api/sessions/$sessionId/"
}