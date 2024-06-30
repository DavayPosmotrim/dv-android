package com.davay.android.feature.roulette.di

import androidx.lifecycle.ViewModel
import com.davay.android.di.ViewModelKey
import com.davay.android.feature.roulette.presentation.RouletteViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface RouletteFragmentModule {
    @IntoMap
    @ViewModelKey(RouletteViewModel::class)
    @Binds
    fun bindVM(impl: RouletteViewModel): ViewModel
}
