package com.davay.android.feature.sessionsmatched.di

import com.davay.android.app.AppComponent
import com.davay.android.di.ScreenComponent
import dagger.Component

@Component(
    dependencies = [AppComponent::class],
    modules = [MatchedSessionListFragmentModule::class]
)
interface MatchedSessionListFragmentComponent : ScreenComponent {
    interface Builder {
        fun appComponent(appComponent: AppComponent): Builder
        fun build(): MatchedSessionListFragmentComponent
    }
}