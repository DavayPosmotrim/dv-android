package com.davay.android.feature.waitsession.data

import com.davay.android.feature.waitsession.domain.WaitSessionOnBoardingRepository

class WaitSessionOnBoardingRepositoryImpl(
    private val firstTimeFlagForWaitSessionStorage: FirstTimeFlagForWaitSessionStorage
) : WaitSessionOnBoardingRepository {
    override fun isFirstTimeLaunch(): Boolean {
        return firstTimeFlagForWaitSessionStorage.isFirstTimeLaunch()
    }

    override fun setFirstTimeLaunch() {
        firstTimeFlagForWaitSessionStorage.setFirstTimeLaunch()
    }
}