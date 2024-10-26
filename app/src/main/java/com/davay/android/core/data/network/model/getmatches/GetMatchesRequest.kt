package com.davay.android.core.data.network.model.getmatches

class GetMatchesRequest(val sessionId: String, val userId: String) {
    val path: String = "api/sessions/$sessionId/get_matched_movies/"
}