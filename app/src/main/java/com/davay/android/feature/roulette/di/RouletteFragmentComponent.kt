package com.davay.android.feature.roulette.di

import com.davay.android.app.AppComponent
import com.davay.android.di.ScreenComponent
import dagger.Component

@Component(
    dependencies = [AppComponent::class],
    modules = [RouletteFragmentModule::class]
)
interface RouletteFragmentComponent : ScreenComponent {
    @Component.Builder
    interface Builder {
        fun appComponent(appComponent: AppComponent): Builder
        fun build(): RouletteFragmentComponent
    }
}
