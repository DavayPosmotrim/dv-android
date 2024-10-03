package com.davay.android.core.data.network

import android.content.Context
import android.util.Log
import com.davay.android.BuildConfig
import com.davay.android.core.data.network.model.NetworkParams.BASE_URL
import com.davay.android.core.data.network.model.NetworkParams.DEVICE_ID_KEY
import com.davay.android.core.data.network.model.NetworkParams.ORIGIN_KEY
import com.davay.android.core.data.network.model.NetworkParams.ORIGIN_BASE_URL
import com.davay.android.core.domain.models.NoInternetConnectionException
import com.davay.android.extensions.isInternetReachable
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.headers
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

abstract class WebsocketKtorNetworkClient<O>(
    private val context: Context
) : WebsocketNetworkClient<O> {
    private val httpClient = HttpClient {
        install(WebSockets) {
            pingInterval = PING_INTERVAL
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

        install(HttpRequestRetry) {
            retryOnServerErrors(MAX_RETRIES_NUM_10)
            retryOnException(maxRetries = MAX_RETRIES_NUM_10)
            exponentialDelay(maxDelayMs = CONNECTION_TIME_OUT_60_SEC)
            modifyRequest { request ->
                request.headers.append("x-retry-count", 2.toString())
            }
        }
    }

    private var session: WebSocketSession? = null

    override suspend fun close() {
        session?.close()
        session = null
    }

    override fun subscribe(deviceId: String, path: String): Flow<O> = flow {
        if (context.isInternetReachable().not()) {
            throw NoInternetConnectionException()
        }

        session = httpClient.webSocketSession(host = BASE_URL, path = path) {
            headers {
                append(DEVICE_ID_KEY, deviceId)
                append(ORIGIN_KEY, ORIGIN_BASE_URL)
            }
        }
        session?.let {
            val incomingMessageFlow = it.incoming.consumeAsFlow()
                .filterIsInstance<Frame.Text>()
                .map { frame -> mapIncomingMessage(frame, Json) }
            emitAll(incomingMessageFlow)
        }
    }

    abstract fun mapIncomingMessage(message: Frame.Text, converter: Json): O

    companion object {
        const val PING_INTERVAL = 20_000L
        const val MAX_RETRIES_NUM_10 = 10
        const val CONNECTION_TIME_OUT_60_SEC = 60_000L
    }
}