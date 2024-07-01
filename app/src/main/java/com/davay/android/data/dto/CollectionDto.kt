package com.davay.android.data.dto

import com.google.gson.annotations.SerializedName

data class CollectionDto(
    @SerializedName("slug") val id: String,
    val name: String,
    @SerializedName("cover") val imgUrl: String
)