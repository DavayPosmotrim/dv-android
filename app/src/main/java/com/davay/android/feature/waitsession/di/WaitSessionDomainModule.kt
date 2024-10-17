package com.davay.android.feature.waitsession.di

import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.core.domain.impl.CommonWebsocketInteractor
import com.davay.android.core.domain.impl.GetUserDataUseCaseImpl
import com.davay.android.core.domain.lounchcontrol.api.FirstTimeFlagRepository
import com.davay.android.core.domain.usecases.GetUserDataUseCase
import com.davay.android.feature.waitsession.domain.SetSessionStatusVotingUseCase
import com.davay.android.feature.waitsession.domain.WaitSessionOnBoardingInteractorImpl
import com.davay.android.feature.waitsession.domain.api.StartVotingSessionStatusRepository
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
    ): GetUserDataUseCase = GetUserDataUseCaseImpl(repository)

    @Provides
    fun provideSetSessionStatusVotingUseCase(
        commonWebsocketInteractor: CommonWebsocketInteractor,
        repository: StartVotingSessionStatusRepository
    ) = SetSessionStatusVotingUseCase(commonWebsocketInteractor, repository)

}