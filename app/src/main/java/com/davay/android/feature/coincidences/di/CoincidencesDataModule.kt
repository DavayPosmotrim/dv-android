package com.davay.android.feature.coincidences.di

import android.content.Context
import android.content.SharedPreferences
import com.davay.android.core.data.database.AppDatabase
import com.davay.android.core.data.impl.GetMatchesRepositoryIml
import com.davay.android.core.data.impl.UserDataRepositoryImpl
import com.davay.android.core.data.network.HttpGetMatchesKtorClient
import com.davay.android.core.data.network.HttpKtorNetworkClient
import com.davay.android.core.data.network.model.getmatches.GetMatchesRequest
import com.davay.android.core.data.network.model.getmatches.GetMatchesResponse
import com.davay.android.core.domain.api.GetMatchesRepository
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.core.domain.lounchcontrol.api.FirstTimeFlagRepository
import com.davay.android.core.domain.lounchcontrol.api.FirstTimeFlagStorage
import com.davay.android.di.prefs.marker.StorageMarker
import com.davay.android.di.prefs.model.PreferencesStorage
import com.davay.android.feature.coincidences.data.CoincidencesStorageImpl
import com.davay.android.feature.coincidences.data.impl.CoincidencesRepositoryImpl
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
    ): HttpKtorNetworkClient<GetMatchesRequest, GetMatchesResponse> {
        return HttpGetMatchesKtorClient(context, httpClient)
    }

    @Provides
    fun provideFirstTimeFlagRepository(firstTimeFlagStorage: FirstTimeFlagStorage): FirstTimeFlagRepository =
        CoincidencesRepositoryImpl(firstTimeFlagStorage)

    @Provides
    fun provideGetMatchesRepository(
        userDataRepository: UserDataRepository,
        httpNetworkClient: HttpKtorNetworkClient<GetMatchesRequest, GetMatchesResponse>,
        appDatabase: AppDatabase
    ): GetMatchesRepository {
        return GetMatchesRepositoryIml(
            userDataRepository,
            httpNetworkClient,
            appDatabase.historyDao()
        )
    }
}