package com.davay.android.core.data.di

import android.util.Log
import com.davay.android.BuildConfig
import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

@Module
class NetworkModule {

    @Suppress("Detekt.LongMethod")
    @Provides
    fun provideHttpClient() = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }

        install(HttpTimeout) {
            requestTimeoutMillis = CONNECTION_TIME_OUT_10_SEC
            connectTimeoutMillis = CONNECTION_TIME_OUT_10_SEC
        }

        install(HttpRequestRetry) {
            retryOnServerErrors(MAX_RETRIES_NUM_5)
            retryOnException(maxRetries = MAX_RETRIES_NUM_5)
            exponentialDelay(maxDelayMs = MAX_REQUEST_DELAY_10_SEC)
            modifyRequest { request ->
                request.headers.append("x-retry-count", 2.toString())
            }
        }

        install(Logging) {
            if (BuildConfig.DEBUG) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.v("Logger Ktor =>", message)
                    }
                }
                level = LogLevel.ALL
            }
        }

        install(ResponseObserver) {
            onResponse { response ->
                Log.d("HTTP status:", "${response.status.value}")
            }
        }

        defaultRequest {
            url(BASE_URL)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }
    }

    private companion object {
        const val CONNECTION_TIME_OUT_10_SEC = 10_000L
        const val MAX_REQUEST_DELAY_10_SEC = 10_000L
        const val MAX_RETRIES_NUM_5 = 5
        const val BASE_URL = "http://80.87.108.90/"
    }
}
