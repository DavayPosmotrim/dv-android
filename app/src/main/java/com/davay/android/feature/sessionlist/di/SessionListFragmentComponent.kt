package com.davay.android.feature.sessionlist.di

import com.davay.android.di.AppComponent
import com.davay.android.di.FragmentScope
import com.davay.android.di.ScreenComponent
import dagger.Component

@Component(
    dependencies = [AppComponent::class],
    modules = [
        SessionListDataModule::class,
        SessionListUseCaseModule::class,
        SessionListFragmentModule::class
    ]
)
@FragmentScope
interface SessionListFragmentComponent : ScreenComponent {
    @Component.Builder
    interface Builder {
        fun appComponent(appComponent: AppComponent): Builder
        fun build(): SessionListFragmentComponent
    }
}