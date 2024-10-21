package com.davay.android.feature.roulette.data.network.model

class StartRouletteRequest(
    sessionId: String,
    val path: String = "api/sessions/$sessionId/get_roulette/",
    val userId: String,
)