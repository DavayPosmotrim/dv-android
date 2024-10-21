package com.davay.android.feature.roulette.di

import com.davay.android.di.AppComponent
import com.davay.android.di.FragmentScope
import com.davay.android.di.ScreenComponent
import dagger.Component

@Component(
    dependencies = [AppComponent::class],
    modules = [
        RouletteDataModule::class,
        RouletteUseCaseModule::class,
        RouletteFragmentModule::class,
    ]
)
@FragmentScope
interface RouletteFragmentComponent : ScreenComponent {
    @Component.Builder
    interface Builder {
        fun appComponent(appComponent: AppComponent): Builder
        fun build(): RouletteFragmentComponent
    }
}
