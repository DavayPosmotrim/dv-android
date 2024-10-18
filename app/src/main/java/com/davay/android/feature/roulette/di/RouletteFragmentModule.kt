package com.davay.android.feature.roulette.di

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.davay.android.core.data.impl.UserDataRepositoryImpl
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.di.ViewModelKey
import com.davay.android.di.prefs.marker.StorageMarker
import com.davay.android.di.prefs.model.PreferencesStorage
import com.davay.android.feature.roulette.presentation.RouletteViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module(includes = [RouletteFragmentModule.CreateSessionApiModule::class])
interface RouletteFragmentModule {
    @IntoMap
    @ViewModelKey(RouletteViewModel::class)
    @Binds
    fun bindVM(impl: RouletteViewModel): ViewModel

    @Module
    class CreateSessionApiModule {
        @Provides
        fun provideUserDataRepository(
            @StorageMarker(PreferencesStorage.USER)
            storage: SharedPreferences
        ): UserDataRepository = UserDataRepositoryImpl(storage)
    }
}
