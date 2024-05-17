package com.davay.android.feature.createsession.di

import androidx.lifecycle.ViewModel
import com.davay.android.di.ViewModelKey
import com.davay.android.feature.createsession.presentation.CompilationsViewModel
import com.davay.android.feature.createsession.presentation.CreateSessionViewModel
import com.davay.android.feature.createsession.presentation.GenreViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface CreateSessionFragmentModule {

    @IntoMap
    @ViewModelKey(CreateSessionViewModel::class)
    @Binds
    fun bindCreateSessionViewModel(impl: CreateSessionViewModel): ViewModel

    @IntoMap
    @ViewModelKey(CompilationsViewModel::class)
    @Binds
    fun bindCompilationsViewModel(impl: CompilationsViewModel): ViewModel

    @IntoMap
    @ViewModelKey(GenreViewModel::class)
    @Binds
    fun bindGenreViewModel(impl: GenreViewModel): ViewModel
}