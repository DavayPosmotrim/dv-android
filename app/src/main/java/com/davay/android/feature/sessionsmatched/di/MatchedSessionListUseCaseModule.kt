package com.davay.android.feature.sessionsmatched.di

import com.davay.android.core.domain.api.SessionsHistoryRepository
import com.davay.android.feature.sessionsmatched.domain.GetSessionsHistoryUseCase
import dagger.Module
import dagger.Provides

@Module
class MatchedSessionListUseCaseModule {
    @Provides
    fun provideGetSessionsHistoryUseCase(repository: SessionsHistoryRepository):
        GetSessionsHistoryUseCase = GetSessionsHistoryUseCase(repository)
}