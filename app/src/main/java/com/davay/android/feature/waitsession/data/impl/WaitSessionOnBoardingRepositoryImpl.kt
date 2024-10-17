package com.davay.android.feature.waitsession.data.impl

import com.davay.android.core.domain.lounchcontrol.api.FirstTimeFlagRepository
import com.davay.android.core.domain.lounchcontrol.api.FirstTimeFlagStorage

class WaitSessionOnBoardingRepositoryImpl(
    private val firstTimeFlagStorage: FirstTimeFlagStorage
) : FirstTimeFlagRepository {
    override fun isFirstTimeLaunch(): Boolean {
        return firstTimeFlagStorage.isFirstTimeLaunch()
    }

    override fun markFirstTimeLaunch() {
        firstTimeFlagStorage.markFirstTimeLaunch()
    }
}