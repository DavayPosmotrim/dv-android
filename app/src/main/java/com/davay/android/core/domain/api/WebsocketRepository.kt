package com.davay.android.core.domain.api

import com.davay.android.core.domain.impl.movieId
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import com.davay.android.core.domain.models.Session
import com.davay.android.core.domain.models.SessionStatus
import com.davay.android.core.domain.models.User
import kotlinx.coroutines.flow.StateFlow

interface WebsocketRepository {
    val usersStateFlow: StateFlow<Result<List<User>, ErrorType>>
    val sessionResultFlow: StateFlow<Result<Session?, ErrorType>>
    val sessionStatusStateFlow: StateFlow<Result<SessionStatus?, ErrorType>>
    val rouletteIdStateFlow: StateFlow<Result<movieId?, ErrorType>>
    val matchesIdStateFlow: StateFlow<Result<movieId?, ErrorType>>

    fun subscribeUsers(sessionId: String): StateFlow<Result<List<User>, ErrorType>>
    suspend fun unsubscribeUsers()
    fun subscribeSessionResult(sessionId: String): StateFlow<Result<Session?, ErrorType>>
    suspend fun unsubscribeSessionResult()
    fun subscribeSessionStatus(sessionId: String): StateFlow<Result<SessionStatus?, ErrorType>>
    suspend fun unsubscribeSessionStatus()
    fun subscribeRouletteId(sessionId: String): StateFlow<Result<movieId?, ErrorType>>
    suspend fun unsubscribeRouletteId()
    fun subscribeMatchesId(sessionId: String): StateFlow<Result<movieId?, ErrorType>>
    suspend fun unsubscribeMatchesId()
}