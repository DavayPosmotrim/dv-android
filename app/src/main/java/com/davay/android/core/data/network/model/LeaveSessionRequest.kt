package com.davay.android.core.data.network.model

class LeaveSessionRequest(
    sessionId: String,
    val path: String = "api/sessions/$sessionId/connection/",
    val userId: String,
)