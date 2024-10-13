package com.davay.android.core.domain.api

import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import com.davay.android.core.domain.models.Session
import com.davay.android.core.domain.models.SessionStatus
import com.davay.android.core.domain.models.User
import kotlinx.coroutines.flow.StateFlow

interface WebsocketRepository {
    val usersStateFlow: StateFlow<Result<List<User>, ErrorType>?>
    val sessionResultFlow: StateFlow<Result<Session, ErrorType>?>
    val sessionStatusStateFlow: StateFlow<Result<SessionStatus, ErrorType>?>
    val rouletteIdStateFlow: StateFlow<Result<Int, ErrorType>?>
    val matchesIdStateFlow: StateFlow<Result<Int, ErrorType>?>

    suspend fun subscribeUsers(sessionId: String)
    suspend fun unsubscribeUsers()
    suspend fun subscribeSessionResult(sessionId: String)
    suspend fun unsubscribeSessionResult()
    suspend fun subscribeSessionStatus(sessionId: String)
    suspend fun unsubscribeSessionStatus()
    suspend fun subscribeRouletteId(sessionId: String)
    suspend fun unsubscribeRouletteId()
    suspend fun subscribeMatchesId(sessionId: String)
    suspend fun unsubscribeMatchesId()
}