package com.davay.android.feature.coincidences.di

import android.content.Context
import android.content.SharedPreferences
import com.davay.android.core.data.database.AppDatabase
import com.davay.android.core.data.impl.UserDataRepositoryImpl
import com.davay.android.core.data.network.HttpKtorNetworkClient
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.core.domain.lounchcontrol.api.FirstTimeFlagRepository
import com.davay.android.core.domain.lounchcontrol.api.FirstTimeFlagStorage
import com.davay.android.di.prefs.marker.StorageMarker
import com.davay.android.di.prefs.model.PreferencesStorage
import com.davay.android.feature.coincidences.data.CoincidencesStorageImpl
import com.davay.android.feature.coincidences.data.impl.CoincidencesRepositoryImpl
import com.davay.android.feature.coincidences.data.network.HttpGetSessionKtorClient
import com.davay.android.feature.coincidences.data.network.models.GetSessionRequest
import com.davay.android.feature.coincidences.data.network.models.GetSessionResponse
import com.davay.android.feature.coincidences.domain.api.CoincidencesRepository
import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient

@Module
class CoincidencesDataModule {

    @Provides
    fun provideCoincidencesStorage(sharedPreferences: SharedPreferences): FirstTimeFlagStorage =
        CoincidencesStorageImpl(sharedPreferences)

    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(
            FirstTimeFlagStorage.STORAGE_NAME,
            Context.MODE_PRIVATE
        )

    @Provides
    fun provideUserDataRepository(
        @StorageMarker(PreferencesStorage.USER)
        storage: SharedPreferences
    ): UserDataRepository = UserDataRepositoryImpl(storage)

    @Provides
    fun provideGetSessionHttpNetworkClient(
        context: Context,
        httpClient: HttpClient
    ): HttpKtorNetworkClient<GetSessionRequest, GetSessionResponse> {
        return HttpGetSessionKtorClient(context, httpClient)
    }

    @Provides
    fun provideCoincidencesRepository(
        userDataRepository: UserDataRepository,
        httpNetworkClient: HttpKtorNetworkClient<GetSessionRequest, GetSessionResponse>,
        appDatabase: AppDatabase,
        firstTimeFlagStorage: FirstTimeFlagStorage
    ): CoincidencesRepository = CoincidencesRepositoryImpl(
        userDataRepository,
        httpNetworkClient,
        appDatabase.historyDao(),
        firstTimeFlagStorage
    )


    @Provides
    fun provideFirstTimeFlagRepository(
        userDataRepository: UserDataRepository,
        httpNetworkClient: HttpKtorNetworkClient<GetSessionRequest, GetSessionResponse>,
        appDatabase: AppDatabase,
        firstTimeFlagStorage: FirstTimeFlagStorage
    ): FirstTimeFlagRepository = CoincidencesRepositoryImpl(
        userDataRepository,
        httpNetworkClient,
        appDatabase.historyDao(),
        firstTimeFlagStorage
    )
}