package com.davay.android.feature.coincidences.di

import com.davay.android.base.usecases.GetData
import com.davay.android.domain.models.ErrorType
import com.davay.android.feature.coincidences.data.TestMovieRepository
import com.davay.android.feature.selectmovie.domain.models.MovieDetailsDemo
import com.davay.android.utils.ConnectionChecker
import dagger.Module
import dagger.Provides
import javax.inject.Named

const val GET_TEST_MOVIE_USE_CASE = "GET_TEST_MOVIE_USE_CASE"
@Module
class CoincidencesDataModule {

    @Provides
    fun testMovieRepository(connectionChecker: ConnectionChecker): TestMovieRepository =
        TestMovieRepository(connectionChecker)

    @Provides
    @Named(GET_TEST_MOVIE_USE_CASE)
    fun getTestMovieUseCase(repo: TestMovieRepository): GetData<MovieDetailsDemo, ErrorType> = repo
}