package com.davay.android.core.domain.api

interface UserDataRepository {
    fun setUserName(userName: String)
    fun setUserId(userId: String)
    fun getUserName(): String
    fun getUserId(): String
}