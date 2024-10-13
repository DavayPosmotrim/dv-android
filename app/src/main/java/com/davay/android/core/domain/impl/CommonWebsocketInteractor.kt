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
    var sessionId: String? = null
        private set

    private suspend fun subscribeSessionStatus(sessionId: String) {
        websocketRepository.subscribeSessionStatus(sessionId)
    }

    private suspend fun unsubscribeSessionStatus() {
        websocketRepository.unsubscribeSessionStatus()
    }

    fun getSessionStatus(): StateFlow<Result<SessionStatus, ErrorType>?> =
        websocketRepository.sessionStatusStateFlow

    private suspend fun subscribeUsers(sessionId: String) {
        websocketRepository.subscribeUsers(sessionId)
    }

    private suspend fun unsubscribeUsers() {
        websocketRepository.unsubscribeUsers()
    }

    fun getUsers(): StateFlow<Result<List<User>, ErrorType>?> =
        websocketRepository.usersStateFlow

    private suspend fun subscribeSessionResult(sessionId: String) {
        websocketRepository.subscribeSessionResult(sessionId)
    }

    private suspend fun unsubscribeSessionResult() {
        websocketRepository.unsubscribeSessionResult()
    }

    fun getSessionResult(): StateFlow<Result<Session, ErrorType>?> =
        websocketRepository.sessionResultFlow

    private suspend fun subscribeRouletteId(sessionId: String) {
        websocketRepository.subscribeRouletteId(sessionId)
    }

    private suspend fun unsubscribeRouletteId() {
        websocketRepository.unsubscribeRouletteId()
    }

    fun getRouletteId(): StateFlow<Result<Int, ErrorType>?> =
        websocketRepository.rouletteIdStateFlow

    private suspend fun subscribeMatchesId(sessionId: String) {
        websocketRepository.subscribeMatchesId(sessionId)
    }

    private suspend fun unsubscribeMatchesId() {
        websocketRepository.unsubscribeMatchesId()
    }

    fun getMatchesId(): StateFlow<Result<Int, ErrorType>?> =
        websocketRepository.matchesIdStateFlow

    suspend fun subscribeWebsockets(sessionId: String) {
        this.sessionId = sessionId
        subscribeSessionStatus(sessionId)
        subscribeUsers(sessionId)
        subscribeSessionResult(sessionId)
        subscribeRouletteId(sessionId)
        subscribeMatchesId(sessionId)
    }

    suspend fun unsubscribeWebsockets() {
        this.sessionId = null
        unsubscribeSessionStatus()
        unsubscribeUsers()
        unsubscribeSessionResult()
        unsubscribeRouletteId()
        unsubscribeMatchesId()
    }
}