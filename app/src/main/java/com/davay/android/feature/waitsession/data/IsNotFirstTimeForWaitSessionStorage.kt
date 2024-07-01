package com.davay.android.feature.waitsession.data

import android.content.SharedPreferences

class IsNotFirstTimeForWaitSessionStorage(
    private val sharedPreferences: SharedPreferences
) {
    fun getIsNotFirstTime(): Boolean {
        return sharedPreferences.getBoolean(IS_NOT_FIRST_TIME_FOR_WAIT_SESSION, false)
    }

    fun setIsNotFirstTime(isNotFirstTime: Boolean) {
        sharedPreferences.edit().putBoolean(
            IS_NOT_FIRST_TIME_FOR_WAIT_SESSION,
            isNotFirstTime
        )
            .apply()
    }

    companion object {
        const val IS_NOT_FIRST_TIME_FOR_WAIT_SESSION = "isNotFirstTimeForWaitSession"
    }
}