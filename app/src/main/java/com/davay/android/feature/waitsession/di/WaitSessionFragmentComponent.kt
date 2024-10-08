package com.davay.android.feature.waitsession.di

import com.davay.android.di.AppComponent
import com.davay.android.di.FragmentScope
import com.davay.android.di.ScreenComponent
import dagger.Component

@Component(
    dependencies = [AppComponent::class],
    modules = [
        WaitSessionFragmentModule::class,
        WaitSessionDataModule::class,
        WaitSessionRepositoryModule::class,
        WaitSessionDomainModule::class
    ]
)
@FragmentScope
interface WaitSessionFragmentComponent : ScreenComponent {

    @Component.Builder
    interface Builder {
        fun appComponent(appComponent: AppComponent): Builder
        fun build(): WaitSessionFragmentComponent
    }
}