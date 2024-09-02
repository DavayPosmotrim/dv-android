package com.davay.android.feature.changename.di

import android.content.Context
import android.content.SharedPreferences
import com.davay.android.core.data.impl.NetworkUserDataRepositoryImpl
import com.davay.android.core.data.impl.UserDataRepositoryImpl
import com.davay.android.core.data.network.HttpKtorNetworkClient
import com.davay.android.core.data.network.UserDataKtorNetworkClient
import com.davay.android.core.data.network.model.UserDataRequest
import com.davay.android.core.domain.api.NetworkUserDataRepository
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.di.prefs.marker.StorageMarker
import com.davay.android.di.prefs.model.PreferencesStorage
import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient
import io.ktor.client.statement.HttpResponse

@Module
class ChangeNameRepositoryModule {
    @Provides
    fun provideUserDataKtorNetworkClient(
        context: Context,
        httpClient: HttpClient
    ): HttpKtorNetworkClient<UserDataRequest, HttpResponse> {
        return UserDataKtorNetworkClient(context, httpClient)
    }

    @Provides
    fun provideNetworkUserDataRepository(
        httpNetworkClient: HttpKtorNetworkClient<UserDataRequest, HttpResponse>,
        userDataRepository: UserDataRepository
    ): NetworkUserDataRepository {
        return NetworkUserDataRepositoryImpl(
            httpNetworkClient = httpNetworkClient,
            userDataRepository = userDataRepository
        )
    }

    @Provides
    fun provideUserDataRepository(
        @StorageMarker(PreferencesStorage.USER)
        storage: SharedPreferences
    ): UserDataRepository = UserDataRepositoryImpl(storage)
}