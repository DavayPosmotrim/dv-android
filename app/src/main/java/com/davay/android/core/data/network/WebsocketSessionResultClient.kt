package com.davay.android.core.data.network

import android.content.Context
import com.davay.android.core.data.dto.MessageSessionResultDto
import com.davay.android.core.data.dto.SessionResultDto
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WebsocketSessionResultClient @Inject constructor(context: Context) :
    WebsocketKtorNetworkClient<SessionResultDto?>(context) {

    override fun mapIncomingMessage(message: Frame.Text, converter: Json): SessionResultDto? {
        return runCatching {
            converter.decodeFromString<MessageSessionResultDto>(message.readText()).sessionResultDto
        }.getOrNull()
    }
}