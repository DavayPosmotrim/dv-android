package com.davay.android.feature.selectmovie.di

import com.davay.android.di.AppComponent
import com.davay.android.di.FragmentScope
import com.davay.android.di.ScreenComponent
import dagger.Component

@Component(
    dependencies = [AppComponent::class],
    modules = [
        SelectMovieDataModule::class,
        SelectMovieDomainModule::class,
        SelectMovieFragmentModule::class
    ]
)
@FragmentScope
interface SelectMovieFragmentComponent : ScreenComponent {

    @Component.Builder
    interface Builder {
        fun appComponent(appComponent: AppComponent): Builder
        fun build(): SelectMovieFragmentComponent
    }
}