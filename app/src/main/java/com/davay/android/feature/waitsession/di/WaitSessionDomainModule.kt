package com.davay.android.feature.waitsession.di

import com.davay.android.core.domain.api.SessionDataRepository
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.core.domain.impl.GetSessionDataUseCase
import com.davay.android.core.domain.impl.GetUserIdUseCase
import com.davay.android.core.domain.lounchcontrol.api.FirstTimeFlagRepository
import com.davay.android.feature.waitsession.domain.WaitSessionOnBoardingInteractorImpl
import com.davay.android.feature.waitsession.domain.api.WaitSessionOnBoardingInteractor
import dagger.Module
import dagger.Provides

@Module
class WaitSessionDomainModule {
    @Provides
    fun provideWaitSessionOnBoardingInteractor(
        repository: FirstTimeFlagRepository
    ): WaitSessionOnBoardingInteractor = WaitSessionOnBoardingInteractorImpl(repository)

    @Provides
    fun provideGetUserIdUseCase(
        repository: UserDataRepository
    ) = GetUserIdUseCase(repository)

    @Provides
    fun provideGetSessionUseCase(
        repository: SessionDataRepository
    ) = GetSessionDataUseCase(repository)
}