package com.davay.android.feature.coincidences.data

import android.content.SharedPreferences
import com.davay.android.core.domain.lounchcontrol.api.FirstTimeFlagStorage

class CoincidencesStorageImpl(
    private val sharedPreferences: SharedPreferences
) : FirstTimeFlagStorage {
    override fun isFirstTimeLaunch(): Boolean {
        return sharedPreferences.getBoolean(FIRST_TIME_FLAG_FOR_COINCIDENCES_KEY, true)
    }

    override fun markFirstTimeLaunch() {
        sharedPreferences.edit().putBoolean(FIRST_TIME_FLAG_FOR_COINCIDENCES_KEY, false).apply()
    }

    private companion object {
        const val FIRST_TIME_FLAG_FOR_COINCIDENCES_KEY = "isFirstTimeCoincidencesHint"
    }
}