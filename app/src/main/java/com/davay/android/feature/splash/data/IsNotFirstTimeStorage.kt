package com.davay.android.feature.splash.data

import android.content.SharedPreferences

class IsNotFirstTimeStorage(
    private val sharedPreferences: SharedPreferences
) {
    fun getIsNotFirstTime(): Boolean {
        return sharedPreferences.getBoolean(IS_NOT_FIRST_TIME, false)
    }

    fun setIsNotFirstTime(isNotFirstTime: Boolean) {
        sharedPreferences.edit().putBoolean(IS_NOT_FIRST_TIME, isNotFirstTime).apply()
    }

    companion object {
        const val IS_NOT_FIRST_TIME = "isNotFirstTime"
    }
}