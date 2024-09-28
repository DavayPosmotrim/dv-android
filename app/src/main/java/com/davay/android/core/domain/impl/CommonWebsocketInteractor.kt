package com.davay.android.core.domain.impl

import com.davay.android.core.domain.api.WebsocketRepository
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import com.davay.android.core.domain.models.Session
import com.davay.android.core.domain.models.SessionStatus
import com.davay.android.core.domain.models.User
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommonWebsocketInteractor @Inject constructor(
    private val websocketRepository: WebsocketRepository,
) {
    fun subscribeSessionStatus(sessionId: String): StateFlow<Result<SessionStatus, ErrorType>?> {
        return websocketRepository.subscribeSessionStatus(sessionId)
    }

    suspend fun unsubscribeSessionStatus() {
        websocketRepository.unsubscribeSessionStatus()
    }

    fun getSessionStatus(): StateFlow<Result<SessionStatus, ErrorType>?> =
        websocketRepository.sessionStatusStateFlow

    fun subscribeUsers(sessionId: String): StateFlow<Result<List<User>, ErrorType>?> {
        return websocketRepository.subscribeUsers(sessionId)
    }

    suspend fun unsubscribeUsers() {
        websocketRepository.unsubscribeUsers()
    }

    fun getUsers(): StateFlow<Result<List<User>, ErrorType>?> =
        websocketRepository.usersStateFlow

    fun subscribeSessionResult(sessionId: String): StateFlow<Result<Session, ErrorType>?> {
        return websocketRepository.subscribeSessionResult(sessionId)
    }

    suspend fun unsubscribeSessionResult() {
        websocketRepository.unsubscribeSessionResult()
    }

    fun getSessionResult(): StateFlow<Result<Session, ErrorType>?> =
        websocketRepository.sessionResultFlow

    fun subscribeRouletteId(sessionId: String): StateFlow<Result<Int, ErrorType>?> {
        return websocketRepository.subscribeRouletteId(sessionId)
    }

    suspend fun unsubscribeRouletteId() {
        websocketRepository.unsubscribeRouletteId()
    }

    fun getRouletteId(): StateFlow<Result<Int, ErrorType>?> =
        websocketRepository.rouletteIdStateFlow

    fun subscribeMatchesId(sessionId: String): StateFlow<Result<Int, ErrorType>?> {
        return websocketRepository.subscribeMatchesId(sessionId)
    }

    suspend fun unsubscribeMatchesId() {
        websocketRepository.unsubscribeMatchesId()
    }

    fun getMatchesId(): StateFlow<Result<Int, ErrorType>?> =
        websocketRepository.matchesIdStateFlow
}