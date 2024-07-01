package com.davay.android.feature.splash.domain


class OnBoardingInteractorImpl(
    private val repository: OnBoardingRepository
) : OnBoardingInteractror {
    override fun setIsNotFirstTime(isNotFirstTime: Boolean) {
        repository.setIsNotFirstTime(isNotFirstTime)
    }

    override fun getIsNotFirstTime(): Boolean {
        return repository.getIsNotFirstTime()
    }
}