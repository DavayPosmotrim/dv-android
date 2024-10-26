package com.davay.android.feature.coincidences.di

import com.davay.android.core.domain.api.GetMatchesRepository
import com.davay.android.core.domain.api.GetSessionRepository
import com.davay.android.core.domain.impl.CommonWebsocketInteractor
import com.davay.android.core.domain.impl.GetMatchesUseCase
import com.davay.android.core.domain.impl.GetSessionDataAndSaveToDbUseCase
import com.davay.android.core.domain.lounchcontrol.api.FirstTimeFlagRepository
import com.davay.android.feature.coincidences.domain.CoincidencesInteractorImpl
import com.davay.android.feature.coincidences.domain.api.CoincidencesInteractor
import dagger.Module
import dagger.Provides

@Module
class CoincidencesDomainModule {

    @Provides
    fun provideCoincidencesInteractor(
        repository: FirstTimeFlagRepository
    ): CoincidencesInteractor = CoincidencesInteractorImpl(repository)

    @Provides
    fun provideGetMatchesUseCase(
        repository: GetMatchesRepository,
        commonWebsocketInteractor: CommonWebsocketInteractor
    ) = GetMatchesUseCase(repository, commonWebsocketInteractor)

    @Provides
    fun provideGetSessionDataAndSaveToDbUseCase(
        repository: GetSessionRepository
    ) = GetSessionDataAndSaveToDbUseCase(repository)
}