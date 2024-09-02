package com.davay.android.feature.changename.di

import com.davay.android.core.domain.api.NetworkUserDataRepository
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.core.domain.impl.GetUserDataUseCaseImpl
import com.davay.android.core.domain.usecases.GetUserDataUseCase
import com.davay.android.feature.changename.domain.usecase.SetUserNameUseCase
import dagger.Module
import dagger.Provides

@Module
class ChangeNameUseCaseModule {
    @Provides
    fun provideGetUserDataUseCase(
        repository: UserDataRepository
    ): GetUserDataUseCase = GetUserDataUseCaseImpl(repository)

    @Provides
    fun provideSetUserNameUseCase(
        repository: NetworkUserDataRepository
    ) = SetUserNameUseCase(repository)
}