package com.davay.android.feature.waitsession.di

import com.davay.android.app.AppComponent
import com.davay.android.di.ScreenComponent
import dagger.Component

@Component(
    dependencies = [AppComponent::class],
    modules = [WaitSessionFragmentModule::class, WaitSessionDataModule::class]
)
interface WaitSessionFragmentComponent : ScreenComponent {

    @Component.Builder
    interface Builder {
        fun appComponent(appComponent: AppComponent): Builder
        fun build(): WaitSessionFragmentComponent
    }
}