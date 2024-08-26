package com.davay.android.feature.coincidences.data

import com.davay.android.core.domain.lounchcontrol.api.FirstTimeFlagRepository
import com.davay.android.core.domain.lounchcontrol.api.FirstTimeFlagStorage

class CoincidencesRepositoryImpl(
    private val firstTimeFlagStorage: FirstTimeFlagStorage
) : FirstTimeFlagRepository {
    override fun isFirstTimeLaunch(): Boolean {
        return firstTimeFlagStorage.isFirstTimeLaunch()
    }

    override fun markFirstTimeLaunch() {
        firstTimeFlagStorage.markFirstTimeLaunch()
    }
}