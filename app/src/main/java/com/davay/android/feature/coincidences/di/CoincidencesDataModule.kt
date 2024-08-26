package com.davay.android.feature.coincidences.di

import android.content.Context
import android.content.SharedPreferences
import com.davay.android.core.domain.lounchcontrol.api.FirstTimeFlagRepository
import com.davay.android.core.domain.lounchcontrol.api.FirstTimeFlagStorage
import com.davay.android.core.domain.mockdata.api.GetData
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.feature.coincidences.data.CoincidencesRepositoryImpl
import com.davay.android.feature.coincidences.data.CoincidencesStorageImpl
import com.davay.android.feature.coincidences.data.TestMovieRepository
import com.davay.android.feature.coincidences.domain.CoincidencesInteractor
import com.davay.android.feature.coincidences.domain.CoincidencesInteractorImpl
import dagger.Module
import dagger.Provides
import javax.inject.Named

const val GET_TEST_MOVIE_USE_CASE = "GET_TEST_MOVIE_USE_CASE"

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
    fun provideCoincidencesRepository(
        firstTimeFlagStorage: FirstTimeFlagStorage
    ): FirstTimeFlagRepository = CoincidencesRepositoryImpl(firstTimeFlagStorage)

    @Provides
    fun provideCoincidencesInteractor(
        repository: FirstTimeFlagRepository
    ): CoincidencesInteractor = CoincidencesInteractorImpl(repository)

    @Provides
    fun testMovieRepository(context: Context): TestMovieRepository =
        TestMovieRepository(context = context)

    @Provides
    @Named(GET_TEST_MOVIE_USE_CASE)
    fun getTestMovieUseCase(repo: TestMovieRepository): GetData<MovieDetails, ErrorType> = repo
}