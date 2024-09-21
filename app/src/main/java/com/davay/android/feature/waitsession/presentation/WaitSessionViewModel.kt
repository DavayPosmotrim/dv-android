package com.davay.android.feature.waitsession.presentation

import com.davay.android.base.BaseViewModel
import com.davay.android.feature.waitsession.domain.WaitSessionOnBoardingInteractor
import javax.inject.Inject

class WaitSessionViewModel @Inject constructor(
    private val waitSessionOnBoardingInteractor: WaitSessionOnBoardingInteractor
) : BaseViewModel() {
    fun isFirstTimeLaunch(): Boolean {
        return waitSessionOnBoardingInteractor.isFirstTimeLaunch()
    }

    fun markFirstTimeLaunch() {
        waitSessionOnBoardingInteractor.markFirstTimeLaunch()
    }
}