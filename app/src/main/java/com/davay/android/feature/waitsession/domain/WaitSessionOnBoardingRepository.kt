package com.davay.android.feature.waitsession.domain

interface WaitSessionOnBoardingRepository {
    fun getIsNotFirstTime(): Boolean
    fun setIsNotFirstTime(isNotFirstTime: Boolean)
}