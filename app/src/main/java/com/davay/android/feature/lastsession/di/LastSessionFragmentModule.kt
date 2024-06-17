package com.davay.android.feature.lastsession.di

import androidx.lifecycle.ViewModel
import com.davay.android.di.ViewModelKey
import com.davay.android.feature.lastsession.presentation.LastSessionViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface LastSessionFragmentModule {

    @IntoMap
    @ViewModelKey(LastSessionViewModel::class)
    @Binds
    fun bindVM(impl: LastSessionViewModel): ViewModel
}
