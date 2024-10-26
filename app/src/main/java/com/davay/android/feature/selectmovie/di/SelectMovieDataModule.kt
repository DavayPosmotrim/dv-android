package com.davay.android.feature.selectmovie.di

import android.content.Context
import android.content.SharedPreferences
import com.davay.android.core.data.database.AppDatabase
import com.davay.android.core.data.impl.GetMatchesRepositoryIml
import com.davay.android.core.data.impl.GetSessionRepositoryIml
import com.davay.android.core.data.impl.UserDataRepositoryImpl
import com.davay.android.core.data.network.HttpGetMatchesKtorClient
import com.davay.android.core.data.network.HttpGetSessionKtorClient
import com.davay.android.core.data.network.HttpKtorNetworkClient
import com.davay.android.core.data.network.model.getmatches.GetMatchesRequest
import com.davay.android.core.data.network.model.getmatches.GetMatchesResponse
import com.davay.android.core.data.network.model.getsession.GetSessionRequest
import com.davay.android.core.data.network.model.getsession.GetSessionResponse
import com.davay.android.core.domain.api.GetMatchesRepository
import com.davay.android.core.domain.api.GetSessionRepository
import com.davay.android.core.domain.api.SessionsHistoryRepository
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.di.prefs.marker.StorageMarker
import com.davay.android.di.prefs.model.PreferencesStorage
import com.davay.android.feature.selectmovie.data.impl.LikeMovieRepositoryImpl
import com.davay.android.feature.selectmovie.data.impl.SelectMovieRepositoryImpl
import com.davay.android.feature.selectmovie.data.network.HttpGetMovieDetailsKtorClient
import com.davay.android.feature.selectmovie.data.network.HttpSetLikeMovieKtorClient
import com.davay.android.feature.selectmovie.data.network.models.GetMovieRequest
import com.davay.android.feature.selectmovie.data.network.models.GetMovieResponse
import com.davay.android.feature.selectmovie.data.network.models.LikeMovieRequest
import com.davay.android.feature.selectmovie.data.network.models.LikeMovieResponse
import com.davay.android.feature.selectmovie.domain.api.LikeMovieRepository
import com.davay.android.feature.selectmovie.domain.api.SelectMovieRepository
import com.davay.android.utils.SorterList
import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient

@Module
class SelectMovieDataModule {
    @Provides
    fun provideGetMovieDetailsHttpNetworkClient(
        context: Context,
        httpClient: HttpClient
    ): HttpKtorNetworkClient<GetMovieRequest, GetMovieResponse> {
        return HttpGetMovieDetailsKtorClient(context, httpClient)
    }

    @Provides
    fun provideSelectMovieRepository(
        httpNetworkClient: HttpKtorNetworkClient<GetMovieRequest, GetMovieResponse>,
        appDatabase: AppDatabase
    ): SelectMovieRepository {
        return SelectMovieRepositoryImpl(
            httpNetworkClient,
            appDatabase.movieIdDao(),
            appDatabase.historyDao()
        )
    }

    @Provides
    fun provideUserDataRepository(
        @StorageMarker(PreferencesStorage.USER)
        storage: SharedPreferences
    ): UserDataRepository = UserDataRepositoryImpl(storage)

    @Provides
    fun provideHttpSetLikeMovieKtorClient(
        context: Context,
        httpClient: HttpClient
    ): HttpKtorNetworkClient<LikeMovieRequest, LikeMovieResponse> {
        return HttpSetLikeMovieKtorClient(context, httpClient)
    }

    @Provides
    fun provideLikeMovieRepository(
        userDataRepository: UserDataRepository,
        httpNetworkClient: HttpKtorNetworkClient<LikeMovieRequest, LikeMovieResponse>,
        appDatabase: AppDatabase
    ): LikeMovieRepository {
        return LikeMovieRepositoryImpl(
            userDataRepository,
            httpNetworkClient,
            appDatabase.movieIdDao()
        )
    }

    @Provides
    fun provideGetMatchesHttpNetworkClient(
        context: Context,
        httpClient: HttpClient
    ): HttpKtorNetworkClient<GetMatchesRequest, GetMatchesResponse> {
        return HttpGetMatchesKtorClient(context, httpClient)
    }

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

    @Provides
    fun provideGetSessionHttpNetworkClient(
        context: Context,
        httpClient: HttpClient
    ): HttpKtorNetworkClient<GetSessionRequest, GetSessionResponse> {
        return HttpGetSessionKtorClient(context, httpClient)
    }

    @Provides
    fun provideGetSessionRepository(
        userDataRepository: UserDataRepository,
        httpNetworkClient: HttpKtorNetworkClient<GetSessionRequest, GetSessionResponse>,
        sessionsHistoryRepository: SessionsHistoryRepository,
        sorterList: SorterList
    ): GetSessionRepository {
        return GetSessionRepositoryIml(
            userDataRepository,
            httpNetworkClient,
            sessionsHistoryRepository,
            sorterList
        )
    }
}