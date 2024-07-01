package com.davay.android.feature.sessionsmatched.di

import androidx.lifecycle.ViewModel
import com.davay.android.di.ViewModelKey
import com.davay.android.feature.sessionsmatched.presentation.MatchedSessionViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface MatchedSessionListFragmentModule {
    @IntoMap
    @ViewModelKey(MatchedSessionViewModel::class)
    @Binds
    fun bindVM(impl: MatchedSessionViewModel): ViewModel
}