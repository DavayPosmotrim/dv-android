package com.davay.android.core.data.network.model

class SessionDataRequest(
    sessionId: String,
    val path: String = "api/sessions/$sessionId/",
    val userId: String
)