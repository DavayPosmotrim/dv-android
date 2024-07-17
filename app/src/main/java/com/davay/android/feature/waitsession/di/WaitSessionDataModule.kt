package com.davay.android.feature.waitsession.di

import android.content.Context
import android.content.SharedPreferences
import com.davay.android.app.core.feature.introduction.domain.FirstTimeFlagRepository
import com.davay.android.app.core.feature.introduction.domain.FirstTimeFlagStorage
import com.davay.android.feature.waitsession.data.WaitSessionOnBoardingRepositoryImpl
import com.davay.android.feature.waitsession.data.WaitSessionStorageImpl
import com.davay.android.feature.waitsession.domain.WaitSessionOnBoardingInteractor
import com.davay.android.feature.waitsession.domain.WaitSessionOnBoardingInteractorImpl
import dagger.Module
import dagger.Provides

@Module
class WaitSessionDataModule {
    @Provides
    fun provideWaitSessionOnBoardingRepository(
        firstTimeFlagStorage: FirstTimeFlagStorage
    ): FirstTimeFlagRepository = WaitSessionOnBoardingRepositoryImpl(firstTimeFlagStorage)

    @Provides
    fun provideWaitSessionOnBoardingInteractor(
        repository: FirstTimeFlagRepository
    ): WaitSessionOnBoardingInteractor = WaitSessionOnBoardingInteractorImpl(repository)

    @Provides
    fun provideIsFirstTimeStorage(
        sharedPreferences: SharedPreferences
    ): FirstTimeFlagStorage = WaitSessionStorageImpl(sharedPreferences)

    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences = context.getSharedPreferences(
        FirstTimeFlagStorage.STORAGE_NAME,
        Context.MODE_PRIVATE
    )
}