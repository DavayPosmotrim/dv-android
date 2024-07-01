package com.davay.android.feature.splash.data

import com.davay.android.feature.splash.domain.OnBoardingRepository

class OnBoardingRepositoryImpl(
    private val isNotFirstTimeStorage: IsNotFirstTimeStorage
) : OnBoardingRepository {
    override fun getIsNotFirstTime(): Boolean {
        return isNotFirstTimeStorage.getIsNotFirstTime()
    }

    override fun setIsNotFirstTime(isNotFirstTime: Boolean) {
        isNotFirstTimeStorage.setIsNotFirstTime(isNotFirstTime)
    }
}