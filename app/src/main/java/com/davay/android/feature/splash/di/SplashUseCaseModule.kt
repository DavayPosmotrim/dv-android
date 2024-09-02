package com.davay.android.feature.splash.di

import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.core.domain.impl.GetUserDataUseCaseImpl
import com.davay.android.core.domain.usecases.GetUserDataUseCase
import dagger.Module
import dagger.Provides

@Module
class SplashUseCaseModule {
    @Provides
    fun provideGetUserDataUseCase(
        repository: UserDataRepository
    ): GetUserDataUseCase = GetUserDataUseCaseImpl(repository)
}