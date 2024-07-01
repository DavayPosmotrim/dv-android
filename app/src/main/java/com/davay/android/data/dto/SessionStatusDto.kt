package com.davay.android.data.dto

import com.google.gson.annotations.SerializedName

enum class SessionStatusDto {
    @SerializedName("waiting")
    WAITING,

    @SerializedName("voting")
    VOTING,

    @SerializedName("closed")
    CLOSED
}