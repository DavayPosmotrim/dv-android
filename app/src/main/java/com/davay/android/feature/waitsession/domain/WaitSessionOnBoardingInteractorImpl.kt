package com.davay.android.feature.waitsession.domain

class WaitSessionOnBoardingInteractorImpl(
    private val repository: WaitSessionOnBoardingRepository
) : WaitSessionOnBoardingInteractror {
    override fun setIsNotFirstTime(isNotFirstTime: Boolean) {
        repository.setIsNotFirstTime(isNotFirstTime)
    }

    override fun getIsNotFirstTime(): Boolean {
        return repository.getIsNotFirstTime()
    }
}