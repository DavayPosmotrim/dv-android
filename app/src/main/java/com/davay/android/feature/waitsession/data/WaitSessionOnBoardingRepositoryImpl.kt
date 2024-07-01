package com.davay.android.feature.waitsession.data

import com.davay.android.feature.waitsession.domain.WaitSessionOnBoardingRepository

class WaitSessionOnBoardingRepositoryImpl(
    private val isNotFirstTimeStorage: IsNotFirstTimeForWaitSessionStorage
) : WaitSessionOnBoardingRepository {
    override fun getIsNotFirstTime(): Boolean {
        return isNotFirstTimeStorage.getIsNotFirstTime()
    }

    override fun setIsNotFirstTime(isNotFirstTime: Boolean) {
        isNotFirstTimeStorage.setIsNotFirstTime(isNotFirstTime)
    }
}