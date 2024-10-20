package com.davay.android.feature.sessionlist.di

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.davay.android.core.data.impl.UserDataRepositoryImpl
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.di.ViewModelKey
import com.davay.android.di.prefs.marker.StorageMarker
import com.davay.android.di.prefs.model.PreferencesStorage
import com.davay.android.feature.sessionlist.presentation.SessionListViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module(
    includes = [
        SessionListFragmentModule.CreateSessionApiModule::class
    ]
)
interface SessionListFragmentModule {
    @IntoMap
    @ViewModelKey(SessionListViewModel::class)
    @Binds
    fun bindVM(impl: SessionListViewModel): ViewModel

    @Module
    class CreateSessionApiModule {
        @Provides
        fun provideUserDataRepository(
            @StorageMarker(PreferencesStorage.USER)
            storage: SharedPreferences
        ): UserDataRepository = UserDataRepositoryImpl(storage)
    }
}