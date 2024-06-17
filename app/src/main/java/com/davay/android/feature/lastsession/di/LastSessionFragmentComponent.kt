package com.davay.android.feature.lastsession.di

import com.davay.android.app.AppComponent
import com.davay.android.di.ScreenComponent
import dagger.Component

@Component(
    dependencies = [AppComponent::class],
    modules = [LastSessionFragmentModule::class]
)
interface LastSessionFragmentComponent : ScreenComponent {

    @Component.Builder
    interface Builder {
        fun appComponent(appComponent: AppComponent): Builder
        fun build(): LastSessionFragmentComponent
    }
}
