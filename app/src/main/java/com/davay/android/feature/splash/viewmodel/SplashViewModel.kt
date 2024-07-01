package com.davay.android.feature.splash.viewmodel

import com.davay.android.base.BaseViewModel
import com.davay.android.feature.splash.domain.OnBoardingInteractror
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val onBoardingInteractror: OnBoardingInteractror
) : BaseViewModel() {
    fun getIsNotFirstTime(): Boolean {
        return onBoardingInteractror.getIsNotFirstTime()
    }

    fun setIsNotFirstTime(isNotFirstTime: Boolean) {
        onBoardingInteractror.setIsNotFirstTime(isNotFirstTime)
    }
}