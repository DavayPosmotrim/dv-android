package com.davay.android.feature.waitsession.domain

interface WaitSessionOnBoardingInteractror {
    fun setIsNotFirstTime(isNotFirstTime: Boolean)
    fun getIsNotFirstTime(): Boolean
}