package com.davay.android.feature.waitsession.data.network.model

class StartVotingSessionRequest(
    sessionId: String,
    val path: String = "api/sessions/$sessionId/start_voting/",
    val userId: String
)