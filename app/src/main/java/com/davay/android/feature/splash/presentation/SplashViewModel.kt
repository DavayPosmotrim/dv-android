package com.davay.android.feature.splash.presentation

import com.davay.android.base.BaseViewModel
import com.davay.android.core.domain.models.UserDataFields
import com.davay.android.core.domain.usecases.GetUserDataUseCase
import com.davay.android.feature.splash.domain.SplashOnBoardingInteractror
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val splashOnBoardingInteractror: SplashOnBoardingInteractror,
    private val getUserData: GetUserDataUseCase
) : BaseViewModel() {
    fun isFirstTimeLaunch(): Boolean {
        return splashOnBoardingInteractror.isFirstTimeLaunch()
    }

    fun markFirstTimeLaunch() {
        splashOnBoardingInteractror.markFirstTimeLaunch()
    }

    fun isNotRegistered(): Boolean {
        return getUserData.getUserData(UserDataFields.UserName()).isNullOrEmpty()
    }
}