package com.davay.android.data.dto

import com.google.gson.annotations.SerializedName

data class MovieDto(
    val id: Int,
    val name: String,
    @SerializedName("poster") val imgUrl: String?
)