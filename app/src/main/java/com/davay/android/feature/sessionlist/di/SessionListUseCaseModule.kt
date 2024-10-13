package com.davay.android.feature.sessionlist.di

import com.davay.android.feature.sessionlist.domain.api.ConnectToSessionRepository
import com.davay.android.feature.sessionlist.domain.usecase.ConnectToSessionUseCase
import dagger.Module
import dagger.Provides

@Module
class SessionListUseCaseModule {
    @Provides
    fun provideConnectToSessionUseCase(repository: ConnectToSessionRepository) =
        ConnectToSessionUseCase(repository)
}