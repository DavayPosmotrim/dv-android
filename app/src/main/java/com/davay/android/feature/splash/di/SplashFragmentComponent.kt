package com.davay.android.feature.splash.di

import com.davay.android.di.AppComponent
import com.davay.android.di.ScreenComponent
import dagger.Component

@Component(
    dependencies = [AppComponent::class],
    modules = [SplashFragmentModule::class, SplashDataModule::class, SplashUseCaseModule::class]
)
interface SplashFragmentComponent : ScreenComponent {

    @Component.Builder
    interface Builder {
        fun appComponent(appComponent: AppComponent): Builder
        fun build(): SplashFragmentComponent
    }
}
