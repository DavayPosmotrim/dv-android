package com.davay.android.di

import com.davay.android.core.data.database.AppDatabase
import com.davay.android.core.data.impl.SessionsHistoryRepositoryImpl
import com.davay.android.core.domain.api.SessionsHistoryRepository
import dagger.Module
import dagger.Provides

@Module
class SessionsHistoryModule {
    @Provides
    fun provideSaveSessionsHistoryRepository(
        appDatabase: AppDatabase
    ): SessionsHistoryRepository {
        return SessionsHistoryRepositoryImpl(appDatabase.historyDao())
    }
}