package com.davay.android.core.data.network.model.getmatches

class GetSessionRequest(val sessionId: String, val userId: String) {
    val path: String = "api/sessions/$sessionId/get_matched_movies/"
}