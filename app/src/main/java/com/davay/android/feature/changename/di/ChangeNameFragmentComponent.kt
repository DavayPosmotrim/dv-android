package com.davay.android.feature.changename.di

import com.davay.android.di.AppComponent
import com.davay.android.di.ScreenComponent
import dagger.Component

@Component(
    dependencies = [AppComponent::class],
    modules = [
        ChangeNameFragmentModule::class,
        ChangeNameRepositoryModule::class,
        ChangeNameUseCaseModule::class
    ]
)
interface ChangeNameFragmentComponent : ScreenComponent {

    @Component.Builder
    interface Builder {
        fun appComponent(appComponent: AppComponent): Builder
        fun build(): ChangeNameFragmentComponent
    }
}