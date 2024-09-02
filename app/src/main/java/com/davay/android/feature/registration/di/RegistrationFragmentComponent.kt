package com.davay.android.feature.registration.di

import com.davay.android.di.AppComponent
import com.davay.android.di.ScreenComponent
import dagger.Component

@Component(
    dependencies = [AppComponent::class],
    modules = [
        RegistrationFragmentModule::class,
        RegistrationDataModule::class,
        RegistrationUseCaseModule::class
    ]
)
interface RegistrationFragmentComponent : ScreenComponent {

    @Component.Builder
    interface Builder {
        fun appComponent(appComponent: AppComponent): Builder
        fun build(): RegistrationFragmentComponent
    }
}
