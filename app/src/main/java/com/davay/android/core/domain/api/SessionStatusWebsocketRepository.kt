package com.davay.android.core.domain.api

import com.davay.android.core.domain.models.SessionStatus
import kotlinx.coroutines.flow.Flow

interface SessionStatusWebsocketRepository {
    fun subscribe(baseUrl: String, path: String): Flow<SessionStatus>
    suspend fun sendMessage(message: SessionStatus)
    suspend fun unsubscribe()
}