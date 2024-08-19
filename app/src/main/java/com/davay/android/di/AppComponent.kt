package com.davay.android.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.davay.android.core.data.database.AppDatabase
import com.davay.android.core.data.database.di.DatabaseModule
import com.davay.android.core.data.di.NetworkModule
import com.davay.android.core.domain.api.SessionsHistoryRepository
import com.davay.android.di.prefs.marker.StorageMarker
import com.davay.android.di.prefs.model.PreferencesStorage
import dagger.BindsInstance
import dagger.Component
import io.ktor.client.HttpClient

@Component(
    modules = [
        NetworkModule::class,
        ContextModule::class,
        DatabaseModule::class,
        EncryptedSharedPreferencesModule::class,
        SessionsHistoryModule::class
    ]
)
interface AppComponent : DIComponent {
    val httpClient: HttpClient
    val context: Context
    val dataBase: AppDatabase
    val sessionsHistoryRepository: SessionsHistoryRepository

    @StorageMarker(PreferencesStorage.USER)
    fun encryptedSharedPreferences(): SharedPreferences

    @Component.Builder
    interface Builder {

        fun build(): AppComponent

        @BindsInstance
        fun app(app: Application): Builder

        fun contextModule(contextModule: ContextModule): Builder

        fun encryptedSharedPreferencesModule(
            encryptedSharedPreferencesModule: EncryptedSharedPreferencesModule
        ): Builder
    }
}

object AppComponentHolder : DataBasedComponentHolder<AppComponent, Application>() {
    override val mode: ComponentHolderMode = ComponentHolderMode.GLOBAL_SINGLETON
    override fun buildComponent(data: Application): AppComponent =
        DaggerAppComponent.builder()
            .app(data)
            .contextModule(ContextModule(data))
            .build()
}
