package com.davay.android.feature.waitsession.di

import android.content.Context
import android.content.SharedPreferences
import com.davay.android.core.data.impl.UserDataRepositoryImpl
import com.davay.android.core.data.network.HttpNetworkClient
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.core.domain.lounchcontrol.api.FirstTimeFlagRepository
import com.davay.android.core.domain.lounchcontrol.api.FirstTimeFlagStorage
import com.davay.android.di.prefs.marker.StorageMarker
import com.davay.android.di.prefs.model.PreferencesStorage
import com.davay.android.feature.waitsession.data.impl.StartVotingSessionStatusRepositoryImpl
import com.davay.android.feature.waitsession.data.impl.WaitSessionOnBoardingRepositoryImpl
import com.davay.android.feature.waitsession.data.impl.WaitSessionStorageImpl
import com.davay.android.feature.waitsession.data.network.HttpStartVotingSessionStatusKtorClient
import com.davay.android.feature.waitsession.data.network.model.StartVotingSessionRequest
import com.davay.android.feature.waitsession.data.network.model.StartVotingSessionResponse
import com.davay.android.feature.waitsession.domain.api.StartVotingSessionStatusRepository
import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient

@Module
class WaitSessionDataModule {
    @Provides
    fun provideWaitSessionOnBoardingRepository(
        firstTimeFlagStorage: FirstTimeFlagStorage
    ): FirstTimeFlagRepository = WaitSessionOnBoardingRepositoryImpl(firstTimeFlagStorage)

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

    @Provides
    fun provideUserDataRepository(
        @StorageMarker(PreferencesStorage.USER) storage: SharedPreferences
    ): UserDataRepository = UserDataRepositoryImpl(storage)

    @Provides
    fun provideStartVotingSessionStatusKtorClient(
        context: Context,
        httpClient: HttpClient
    ): HttpNetworkClient<StartVotingSessionRequest, StartVotingSessionResponse> {
        return HttpStartVotingSessionStatusKtorClient(context, httpClient)
    }

    @Provides
    fun provideStartVotingSessionStatus(
        userDataRepository: UserDataRepository,
        httpClient: HttpNetworkClient<StartVotingSessionRequest, StartVotingSessionResponse>
    ): StartVotingSessionStatusRepository =
        StartVotingSessionStatusRepositoryImpl(userDataRepository, httpClient)

}