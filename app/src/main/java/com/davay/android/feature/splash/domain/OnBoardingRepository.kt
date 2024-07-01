package com.davay.android.feature.splash.domain

interface OnBoardingRepository {
    fun getIsNotFirstTime(): Boolean
    fun setIsNotFirstTime(isNotFirstTime: Boolean)
}