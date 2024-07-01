package com.davay.android.data.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    val name: String,
    @SerializedName("device_id") val userId: String
)