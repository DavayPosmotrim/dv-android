package com.davay.android.core.data.impl

import android.content.SharedPreferences
import com.davay.android.core.domain.api.UserDataRepository

class UserDataRepositoryImpl(
    private val userDataStorage: SharedPreferences
) : UserDataRepository {
    override fun setUserName(userName: String) {
        userDataStorage
            .edit()
            .putString(USER_NAME, userName)
            .apply()
    }

    override fun setUserId(userId: String) {
        userDataStorage
            .edit()
            .putString(USER_ID, userId)
            .apply()
    }

    override fun getUserName(): String {
        return userDataStorage.getString(USER_NAME, "") ?: ""
    }

    override fun getUserId(): String {
        return userDataStorage.getString(USER_ID, "") ?: ""
    }

    companion object {
        const val USER_ID = "userId"
        const val USER_NAME = "userName"
    }
}