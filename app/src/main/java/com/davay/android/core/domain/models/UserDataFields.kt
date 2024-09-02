package com.davay.android.core.domain.models

sealed interface UserDataFields {
    class UserId(val userId: String = "") : UserDataFields
    class UserName(val userName: String = "") : UserDataFields
}