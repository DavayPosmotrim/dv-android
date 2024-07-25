package com.davay.android.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenreDto(
    @SerialName("name") val name: String
)
