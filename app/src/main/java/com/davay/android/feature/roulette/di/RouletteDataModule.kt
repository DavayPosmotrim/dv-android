package com.davay.android.feature.roulette.di

import android.content.Context
import com.davay.android.core.data.database.AppDatabase
import com.davay.android.core.data.network.HttpKtorNetworkClient
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.feature.roulette.data.impl.RouletteMoviesRepositoryImpl
import com.davay.android.feature.roulette.data.impl.StartRouletteRepositoryImpl
import com.davay.android.feature.roulette.data.network.StartRouletteKtorNetworkClient
import com.davay.android.feature.roulette.data.network.model.StartRouletteRequest
import com.davay.android.feature.roulette.data.network.model.StartRouletteResponse
import com.davay.android.feature.roulette.domain.api.RouletteMoviesRepository
import com.davay.android.feature.roulette.domain.api.StartRouletteRepository
import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient

@Module
class RouletteDataModule {
    @Provides
    fun provideStartRouletteHttpNetworkClient(
        context: Context,
        httpClient: HttpClient
    ): HttpKtorNetworkClient<StartRouletteRequest, StartRouletteResponse> {
        return StartRouletteKtorNetworkClient(context, httpClient)
    }

    @Provides
    fun provideStartRouletteRepository(
        httpNetworkClient: HttpKtorNetworkClient<StartRouletteRequest, StartRouletteResponse>,
        userDataRepository: UserDataRepository,
    ): StartRouletteRepository {
        return StartRouletteRepositoryImpl(
            userDataRepository,
            httpNetworkClient,
        )
    }

    @Provides
    fun provideRouletteMoviesRepository(
        appDatabase: AppDatabase
    ): RouletteMoviesRepository {
        return RouletteMoviesRepositoryImpl(appDatabase.historyDao())
    }
}