package com.davay.android.feature.waitsession.di

import android.content.Context
import android.content.SharedPreferences
import com.davay.android.core.data.impl.LeaveSessionRepositoryImpl
import com.davay.android.core.data.impl.SessionDataRepositoryImpl
import com.davay.android.core.data.impl.UserDataRepositoryImpl
import com.davay.android.core.data.network.HttpKtorNetworkClient
import com.davay.android.core.data.network.LeaveSessionKtorNetworkClient
import com.davay.android.core.data.network.model.LeaveSessionRequest
import com.davay.android.core.data.network.model.LeaveSessionResponse
import com.davay.android.core.data.network.model.SessionDataRequest
import com.davay.android.core.data.network.model.SessionDataResponse
import com.davay.android.core.domain.api.LeaveSessionRepository
import com.davay.android.core.domain.api.SessionDataRepository
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.core.domain.lounchcontrol.api.FirstTimeFlagRepository
import com.davay.android.core.domain.lounchcontrol.api.FirstTimeFlagStorage
import com.davay.android.di.prefs.marker.StorageMarker
import com.davay.android.di.prefs.model.PreferencesStorage
import com.davay.android.feature.waitsession.data.WaitSessionOnBoardingRepositoryImpl
import com.davay.android.feature.waitsession.data.WaitSessionStorageImpl
import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient

@Module
class WaitSessionRepositoryModule {
    @Provides
    fun provideWaitSessionOnBoardingRepository(
        firstTimeFlagStorage: FirstTimeFlagStorage
    ): FirstTimeFlagRepository = WaitSessionOnBoardingRepositoryImpl(firstTimeFlagStorage)

    @Provides
    fun provideUserDataRepository(
        @StorageMarker(PreferencesStorage.USER)
        storage: SharedPreferences
    ): UserDataRepository = UserDataRepositoryImpl(storage)

    @Provides
    fun provideSessionDataRepository(
        userDataRepository: UserDataRepository,
        httpClient: HttpKtorNetworkClient<SessionDataRequest, SessionDataResponse>
    ): SessionDataRepository = SessionDataRepositoryImpl(userDataRepository, httpClient)
}