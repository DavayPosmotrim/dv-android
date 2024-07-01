package com.davay.android.feature.splash.domain

interface OnBoardingInteractror {
    fun setIsNotFirstTime(isNotFirstTime: Boolean)
    fun getIsNotFirstTime(): Boolean
}