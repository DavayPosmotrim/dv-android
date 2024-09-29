package com.davay.android.core.data.network

import android.content.Context
import com.davay.android.core.data.dto.MessageUsersDto
import com.davay.android.core.data.dto.UserDto
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WebsocketUsersClient @Inject constructor(context: Context) :
    WebsocketKtorNetworkClient<List<UserDto>?>(context) {

    override fun mapIncomingMessage(message: Frame.Text, converter: Json): List<UserDto>? {
        return runCatching {
            converter.decodeFromString<MessageUsersDto>(message.readText()).userList
        }.getOrNull()
    }
}