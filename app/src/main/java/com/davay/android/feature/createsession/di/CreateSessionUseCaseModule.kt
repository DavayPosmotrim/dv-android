package com.davay.android.feature.createsession.di

import com.davay.android.feature.createsession.domain.api.CreateSessionRepository
import com.davay.android.feature.createsession.domain.usecase.GetCollectionsUseCase
import com.davay.android.feature.createsession.domain.usecase.GetGenresUseCase
import com.davay.android.feature.sessionlist.domain.api.ConnectToSessionRepository
import com.davay.android.feature.sessionlist.domain.usecase.ConnectToSessionUseCase
import dagger.Module
import dagger.Provides

@Module
class CreateSessionUseCaseModule {
    @Provides
    fun provideGetCollectionsUseCase(repository: CreateSessionRepository) =
        GetCollectionsUseCase(repository)

    @Provides
    fun provideGetGenresUseCase(repository: CreateSessionRepository) = GetGenresUseCase(repository)

    @Provides
    fun provideConnectToSessionUseCase(repository: ConnectToSessionRepository) =
        ConnectToSessionUseCase(repository)
}
