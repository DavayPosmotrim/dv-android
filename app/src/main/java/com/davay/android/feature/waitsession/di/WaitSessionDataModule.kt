package com.davay.android.feature.waitsession.di

import android.content.Context
import android.content.SharedPreferences
import com.davay.android.core.data.network.HttpKtorNetworkClient
import com.davay.android.core.data.network.LeaveSessionKtorNetworkClient
import com.davay.android.core.data.network.SessionDataKtorNetworkClient
import com.davay.android.core.data.network.model.LeaveSessionRequest
import com.davay.android.core.data.network.model.LeaveSessionResponse
import com.davay.android.core.data.network.model.SessionDataRequest
import com.davay.android.core.data.network.model.SessionDataResponse
import com.davay.android.core.domain.lounchcontrol.api.FirstTimeFlagStorage
import com.davay.android.feature.waitsession.data.WaitSessionStorageImpl
import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient

@Module
class WaitSessionDataModule {
    @Provides
    fun provideSessionDataHttpClient(
        context: Context,
        httpClient: HttpClient
    ): HttpKtorNetworkClient<SessionDataRequest, SessionDataResponse> =
        SessionDataKtorNetworkClient(context, httpClient)

    @Provides
    fun provideIsFirstTimeStorage(
        sharedPreferences: SharedPreferences
    ): FirstTimeFlagStorage = WaitSessionStorageImpl(sharedPreferences)

    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(
            FirstTimeFlagStorage.STORAGE_NAME,
            Context.MODE_PRIVATE
        )
}