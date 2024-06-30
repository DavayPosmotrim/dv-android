package com.davay.android.feature.roulette.presentation.model

data class UserRouletteModel(
    val id: Int,
    val name: String,
    @Suppress("DataClassShouldBeImmutable")
    var isConnected: Boolean = false
)
