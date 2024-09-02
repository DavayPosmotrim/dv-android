package com.davay.android.core.data.network.model

import com.davay.android.core.data.dto.UserDto

sealed interface UserDataRequest {
    class Registration(val userData: UserDto) : UserDataRequest
    class ChangeName(val userData: UserDto) : UserDataRequest
}