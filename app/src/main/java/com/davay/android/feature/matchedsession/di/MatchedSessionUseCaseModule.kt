package com.davay.android.feature.matchedsession.di

import com.davay.android.core.domain.api.SessionsHistoryRepository
import com.davay.android.feature.matchedsession.domain.SessionWithMoviesUseCase
import dagger.Module
import dagger.Provides

@Module
class MatchedSessionUseCaseModule {
    @Provides
    fun provideSessionWithMoviesUseCase(repository: SessionsHistoryRepository):
        SessionWithMoviesUseCase = SessionWithMoviesUseCase(repository)
}