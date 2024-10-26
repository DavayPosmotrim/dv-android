package com.davay.android.feature.selectmovie.di

import com.davay.android.core.domain.api.GetMatchesRepository
import com.davay.android.core.domain.api.GetSessionRepository
import com.davay.android.core.domain.impl.CommonWebsocketInteractor
import com.davay.android.core.domain.impl.GetMatchesUseCase
import com.davay.android.core.domain.impl.GetSessionDataAndSaveToDbUseCase
import com.davay.android.feature.selectmovie.domain.FilterDislikedMovieListUseCase
import com.davay.android.feature.selectmovie.domain.GetMovieDetailsByIdUseCase
import com.davay.android.feature.selectmovie.domain.GetMovieIdListSizeUseCase
import com.davay.android.feature.selectmovie.domain.GetMovieListUseCase
import com.davay.android.feature.selectmovie.domain.LikeMovieInteractor
import com.davay.android.feature.selectmovie.domain.api.LikeMovieRepository
import com.davay.android.feature.selectmovie.domain.api.SelectMovieRepository
import dagger.Module
import dagger.Provides

@Module
class SelectMovieDomainModule {
    @Provides
    fun provideFilterDislikedMovieListUseCase(repository: SelectMovieRepository) =
        FilterDislikedMovieListUseCase(repository)

    @Provides
    fun provideGetMovieIdListSizeUseCase(repository: SelectMovieRepository) =
        GetMovieIdListSizeUseCase(repository)

    @Provides
    fun provideGetMovieListUseCase(repository: SelectMovieRepository) =
        GetMovieListUseCase(repository)

    @Provides
    fun provideLikeMovieInteractor(
        commonWebsocketInteractor: CommonWebsocketInteractor,
        repository: LikeMovieRepository
    ) = LikeMovieInteractor(commonWebsocketInteractor, repository)

    @Provides
    fun provideGetMovieDetailsByIdUseCase(
        repository: SelectMovieRepository
    ) = GetMovieDetailsByIdUseCase(repository)

    @Provides
    fun provideGetMatchesUseCase(
        repository: GetMatchesRepository,
        commonWebsocketInteractor: CommonWebsocketInteractor
    ) = GetMatchesUseCase(repository, commonWebsocketInteractor)

    @Provides
    fun provideGetSessionDataAndSaveToDbUseCase(
        repository: GetSessionRepository
    ) = GetSessionDataAndSaveToDbUseCase(repository)
}