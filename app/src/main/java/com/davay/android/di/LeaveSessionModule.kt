package com.davay.android.di

import android.content.Context
import com.davay.android.core.data.impl.LeaveSessionRepositoryImpl
import com.davay.android.core.data.network.HttpKtorNetworkClient
import com.davay.android.core.data.network.LeaveSessionKtorNetworkClient
import com.davay.android.core.data.network.model.LeaveSessionRequest
import com.davay.android.core.data.network.model.LeaveSessionResponse
import com.davay.android.core.domain.api.LeaveSessionRepository
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.core.domain.impl.LeaveSessionUseCase
import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient

@Module
class LeaveSessionModule {
    @Provides
    fun provideLeaveSessionHttpNetworkClient(
        context: Context,
        httpClient: HttpClient
    ): HttpKtorNetworkClient<LeaveSessionRequest, LeaveSessionResponse> {
        return LeaveSessionKtorNetworkClient(context, httpClient)
    }

    @Provides
    fun provideLeaveSessionRepository(
        httpNetworkClient: HttpKtorNetworkClient<LeaveSessionRequest, LeaveSessionResponse>,
        userDataRepository: UserDataRepository,
    ): LeaveSessionRepository {
        return LeaveSessionRepositoryImpl(
            userDataRepository,
            httpNetworkClient,
        )
    }

    @Provides
    fun provideLeaveSessionUseCase(repository: LeaveSessionRepository) =
        LeaveSessionUseCase(repository)
}