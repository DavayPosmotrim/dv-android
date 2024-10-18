package com.davay.android.feature.waitsession.data.impl

import android.content.SharedPreferences
import com.davay.android.core.domain.lounchcontrol.api.FirstTimeFlagStorage

class WaitSessionStorageImpl(
    private val sharedPreferences: SharedPreferences
) : FirstTimeFlagStorage {
    override fun isFirstTimeLaunch(): Boolean {
        return sharedPreferences.getBoolean(FIRST_TIME_FLAG_FOR_WAIT_SESSION_KEY, true)
    }

    override fun markFirstTimeLaunch() {
        sharedPreferences.edit().putBoolean(
            FIRST_TIME_FLAG_FOR_WAIT_SESSION_KEY,
            false
        )
            .apply()
    }

    private companion object {
        const val FIRST_TIME_FLAG_FOR_WAIT_SESSION_KEY = "isFirstTimeWaitSessionIntroduction"
    }
}