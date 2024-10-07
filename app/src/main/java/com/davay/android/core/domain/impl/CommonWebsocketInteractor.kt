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

typealias movieId = Int

@Singleton
class CommonWebsocketInteractor @Inject constructor(
    private val websocketRepository: WebsocketRepository,
) {
    private var sessionId: String = ""

    fun getSessionId(): String = sessionId

    fun updateSessionId(sessionId: String) {
        this.sessionId = sessionId
    }


    fun subscribeSessionStatus(sessionId: String = this.sessionId): StateFlow<Result<SessionStatus?, ErrorType>> {
        return websocketRepository.subscribeSessionStatus(sessionId)
    }

    suspend fun unsubscribeAll() {
        websocketRepository.unsubscribeSessionStatus()
        websocketRepository.unsubscribeSessionResult()
        websocketRepository.unsubscribeUsers()
        websocketRepository.unsubscribeRouletteId()
        websocketRepository.unsubscribeMatchesId()
    }

    suspend fun unsubscribeSessionStatus() {
        websocketRepository.unsubscribeSessionStatus()
    }

    fun getSessionStatus(): StateFlow<Result<SessionStatus?, ErrorType>> =
        websocketRepository.sessionStatusStateFlow

    fun subscribeUsers(sessionId: String = this.sessionId): StateFlow<Result<List<User>, ErrorType>?> {
        return websocketRepository.subscribeUsers(sessionId)
    }

    suspend fun unsubscribeUsers() {
        websocketRepository.unsubscribeUsers()
    }

    fun getUsers(): StateFlow<Result<List<User>, ErrorType>?> =
        websocketRepository.usersStateFlow

    fun subscribeSessionResult(sessionId: String = this.sessionId): StateFlow<Result<Session?, ErrorType>> {
        return websocketRepository.subscribeSessionResult(sessionId)
    }

    suspend fun unsubscribeSessionResult() {
        websocketRepository.unsubscribeSessionResult()
    }

    fun getSessionResult(): StateFlow<Result<Session?, ErrorType>> =
        websocketRepository.sessionResultFlow

    fun subscribeRouletteId(sessionId: String = this.sessionId): StateFlow<Result<movieId?, ErrorType>> {
        return websocketRepository.subscribeRouletteId(sessionId)
    }

    suspend fun unsubscribeRouletteId() {
        websocketRepository.unsubscribeRouletteId()
    }

    fun getRouletteId(): StateFlow<Result<movieId?, ErrorType>> =
        websocketRepository.rouletteIdStateFlow

    fun subscribeMatchesId(sessionId: String = this.sessionId): StateFlow<Result<movieId?, ErrorType>> {
        return websocketRepository.subscribeMatchesId(sessionId)
    }

    suspend fun unsubscribeMatchesId() {
        websocketRepository.unsubscribeMatchesId()
    }

    fun getMatchesId(): StateFlow<Result<movieId?, ErrorType>> =
        websocketRepository.matchesIdStateFlow
}