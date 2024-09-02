package com.davay.android.feature.registration.di

import com.davay.android.core.domain.api.NetworkUserDataRepository
import com.davay.android.feature.registration.domain.usecase.RegistrationUseCase
import dagger.Module
import dagger.Provides

@Module
class RegistrationUseCaseModule {
    @Provides
    fun provideRegistrationUseCase(repository: NetworkUserDataRepository) =
        RegistrationUseCase(repository)
}