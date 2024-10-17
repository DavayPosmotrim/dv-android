package com.davay.android.feature.coincidences.di

import com.davay.android.core.domain.impl.CommonWebsocketInteractor
import com.davay.android.core.domain.lounchcontrol.api.FirstTimeFlagRepository
import com.davay.android.feature.coincidences.domain.CoincidencesInteractorImpl
import com.davay.android.feature.coincidences.domain.GetMatchesUseCase
import com.davay.android.feature.coincidences.domain.api.CoincidencesInteractor
import com.davay.android.feature.coincidences.domain.api.CoincidencesRepository
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
        repository: CoincidencesRepository,
        commonWebsocketInteractor: CommonWebsocketInteractor
    ) = GetMatchesUseCase(repository, commonWebsocketInteractor)
}