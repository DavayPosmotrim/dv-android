package com.davay.android.feature.selectmovie.di

import com.davay.android.feature.selectmovie.domain.GetMovieListUseCase
import com.davay.android.feature.selectmovie.domain.api.SelectMovieRepository
import dagger.Module
import dagger.Provides

@Module
class SelectMovieUseCaseModule {
    @Provides
    fun provideGetMovieDetailsUseCase(repository: SelectMovieRepository) =
        GetMovieListUseCase(repository)
}