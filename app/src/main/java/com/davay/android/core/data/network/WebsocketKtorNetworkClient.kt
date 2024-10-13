package com.davay.android.core.data.network

import android.util.Log
import com.davay.android.BuildConfig
import com.davay.android.core.data.network.model.NetworkParams.BASE_URL
import com.davay.android.core.data.network.model.NetworkParams.DEVICE_ID_KEY
import com.davay.android.core.data.network.model.NetworkParams.ORIGIN_KEY
import com.davay.android.core.data.network.model.NetworkParams.ORIGIN_VALUE
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.headers
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

abstract class WebsocketKtorNetworkClient<O> : WebsocketNetworkClient<O> {
    private val httpClient = HttpClient {
        install(WebSockets) {
            pingInterval = PING_INTERVAL_MS_10_000L
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
    }
    private var session: WebSocketSession? = null

    private val reconnectDelay = RECONNECT_DELAY_MS_5_000L
    private var numberReconnections = 0
    private var shouldReconnect = true

    override suspend fun close() {
        shouldReconnect = false
        session?.close()
        session = null
        numberReconnections = 0
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "${this.javaClass.simpleName} закрыт")
        }
    }

    @Suppress("TooGenericExceptionCaught", "TooGenericExceptionThrown", "CognitiveComplexMethod")
    override fun subscribe(deviceId: String, path: String): Flow<O> = flow {
        shouldReconnect = true
        while (shouldReconnect) {
            try {
                session = httpClient.webSocketSession(host = BASE_URL, path = path) {
                    headers {
                        append(DEVICE_ID_KEY, deviceId)
                        append(ORIGIN_KEY, ORIGIN_VALUE)
                    }
                }

                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Соединение установлено")
                }
                numberReconnections = 0

                val incomingMessageFlow =
                    (session as DefaultClientWebSocketSession).incoming.consumeAsFlow()
                        .filterIsInstance<Frame.Text>()
                        .map { frame -> mapIncomingMessage(frame, Json) }
                emitAll(incomingMessageFlow)

            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "Ошибка подключения: ${e.message}")
                }
                numberReconnections++
                if (numberReconnections >= MAX_NUMBER_RECONNECTIONS) {
                    throw Exception(String.format(ERROR_MESSAGE, numberReconnections))
                }
                delay(reconnectDelay)
            }
        }
    }

    abstract fun mapIncomingMessage(message: Frame.Text, converter: Json): O

    companion object {
        const val PING_INTERVAL_MS_10_000L = 10_000L
        const val RECONNECT_DELAY_MS_5_000L = 5_000L
        const val MAX_NUMBER_RECONNECTIONS = 3
        const val ERROR_MESSAGE = "Failed to connect after %s attempts"
        val TAG = WebsocketKtorNetworkClient::class.simpleName
    }
}