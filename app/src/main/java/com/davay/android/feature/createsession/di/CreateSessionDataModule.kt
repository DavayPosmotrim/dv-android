package com.davay.android.feature.createsession.di

import android.content.Context
import com.davay.android.core.data.database.AppDatabase
import com.davay.android.core.data.network.HttpKtorNetworkClient
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.feature.createsession.data.CreateSessionRepositoryImpl
import com.davay.android.feature.createsession.data.network.CreateSessionRequest
import com.davay.android.feature.createsession.data.network.CreateSessionResponse
import com.davay.android.feature.createsession.data.network.HttpCreateSessionKtorClient
import com.davay.android.feature.createsession.domain.api.CreateSessionRepository
import com.davay.android.feature.sessionlist.data.ConnectToSessionRepositoryImpl
import com.davay.android.feature.sessionlist.data.network.ConnectToSessionRequest
import com.davay.android.feature.sessionlist.data.network.ConnectToSessionResponse
import com.davay.android.feature.sessionlist.data.network.HttpConnectToSessionKtorClient
import com.davay.android.feature.sessionlist.domain.api.ConnectToSessionRepository
import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient

@Module
class CreateSessionDataModule {

    @Provides
    fun provideCreateSessionHttpNetworkClient(
        context: Context,
        httpClient: HttpClient
    ): HttpKtorNetworkClient<CreateSessionRequest, CreateSessionResponse> {
        return HttpCreateSessionKtorClient(context, httpClient)
    }

    @Provides
    fun provideConnectToSessionHttpNetworkClient(
        context: Context,
        httpClient: HttpClient
    ): HttpKtorNetworkClient<ConnectToSessionRequest, ConnectToSessionResponse> {
        return HttpConnectToSessionKtorClient(context, httpClient)
    }

    @Provides
    fun provideCreateSessionRepository(
        httpNetworkClient: HttpKtorNetworkClient<CreateSessionRequest, CreateSessionResponse>,
        userDataRepository: UserDataRepository,
        appDatabase: AppDatabase
    ): CreateSessionRepository {
        return CreateSessionRepositoryImpl(
            httpNetworkClient,
            userDataRepository,
            appDatabase.movieIdDao()
        )
    }

    @Provides
    fun provideConnectToSessionRepository(
        httpNetworkClient: HttpKtorNetworkClient<ConnectToSessionRequest, ConnectToSessionResponse>,
        userDataRepository: UserDataRepository,
        appDatabase: AppDatabase
    ): ConnectToSessionRepository {
        return ConnectToSessionRepositoryImpl(
            appDatabase.movieIdDao(),
            userDataRepository,
            httpNetworkClient,
        )
    }
}
