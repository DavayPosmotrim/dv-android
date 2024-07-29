package com.davay.android.feature.createsession.domain.model

data class GenreSelect(
    val id: Int,
    val name: String,
    @Suppress("DataClassShouldBeImmutable")
    var isSelected: Boolean = false,
)