package com.davay.android.data.impl

import android.content.SharedPreferences
import com.davay.android.data.repositories.GetUserNameRepository

class GetUserNameRepositoryImpl(
    private val storage: SharedPreferences
) : GetUserNameRepository {
    override fun getUserName(): String {
        var value = String()
        storage.getString("userName", String())?.let { value = it }
        return value
    }
}