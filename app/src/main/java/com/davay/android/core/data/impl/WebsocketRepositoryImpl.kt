package com.davay.android.core.data.impl

import com.davay.android.BuildConfig
import com.davay.android.core.data.converters.toDomain
import com.davay.android.core.data.dto.SessionResultDto
import com.davay.android.core.data.dto.SessionStatusDto
import com.davay.android.core.data.dto.UserDto
import com.davay.android.core.data.network.WebsocketNetworkClient
import com.davay.android.core.data.network.model.NetworkParams.PATH_MATCHES
import com.davay.android.core.data.network.model.NetworkParams.PATH_ROULETTE
import com.davay.android.core.data.network.model.NetworkParams.PATH_SESSION_RESULT
import com.davay.android.core.data.network.model.NetworkParams.PATH_SESSION_STATUS
import com.davay.android.core.data.network.model.NetworkParams.PATH_USERS
import com.davay.android.core.domain.api.SessionsHistoryRepository
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.core.domain.api.WebsocketRepository
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import com.davay.android.core.domain.models.Session
import com.davay.android.core.domain.models.SessionStatus
import com.davay.android.core.domain.models.User
import com.davay.android.di.MatchesIdClient
import com.davay.android.di.RouletteIdClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("LargeClass", "LongParameterList")
class WebsocketRepositoryImpl @Inject constructor(
    private val websocketUsersClient: WebsocketNetworkClient<List<UserDto>?>,
    private val websocketSessionResultClient: WebsocketNetworkClient<SessionResultDto?>,
    private val websocketSessionStatusClient: WebsocketNetworkClient<SessionStatusDto?>,
    @RouletteIdClient private val websocketRouletteIdClient: WebsocketNetworkClient<Int?>,
    @MatchesIdClient private val websocketMatchesIdClient: WebsocketNetworkClient<Int?>,
    private val userDataRepository: UserDataRepository,
    private val sessionsHistoryRepository: SessionsHistoryRepository,
) : WebsocketRepository {

    private val deviceId = userDataRepository.getUserId()
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _usersStateFlow = MutableStateFlow<Result<List<User>, ErrorType>?>(null)
    override val usersStateFlow: StateFlow<Result<List<User>, ErrorType>?> get() = _usersStateFlow

    private val _sessionResultFlow = MutableStateFlow<Result<Session, ErrorType>?>(null)
    override val sessionResultFlow: StateFlow<Result<Session, ErrorType>?> get() = _sessionResultFlow

    private val _sessionStatusStateFlow = MutableStateFlow<Result<SessionStatus, ErrorType>?>(null)
    override val sessionStatusStateFlow: StateFlow<Result<SessionStatus, ErrorType>?> get() = _sessionStatusStateFlow

    private val _rouletteIdStateFlow = MutableStateFlow<Result<Int, ErrorType>?>(null)
    override val rouletteIdStateFlow: StateFlow<Result<Int, ErrorType>?> get() = _rouletteIdStateFlow

    private val _matchesIdStateFlow = MutableStateFlow<Result<Int, ErrorType>?>(null)
    override val matchesIdStateFlow: StateFlow<Result<Int, ErrorType>?> get() = _matchesIdStateFlow

    override suspend fun subscribeUsers(sessionId: String) {
        repositoryScope.launch {
            subscribeUsersFlow(sessionId).collect { result ->
                _usersStateFlow.update { result }
            }
        }
    }

    private fun subscribeUsersFlow(sessionId: String): Flow<Result<List<User>, ErrorType>> = flow {
        @Suppress("TooGenericExceptionCaught")
        try {
            websocketUsersClient.subscribe(deviceId, "$sessionId$PATH_USERS").collect { list ->
                if (list != null) {
                    emit(Result.Success(list.map { it.toDomain() }))
                } else {
                    emit(Result.Error(ErrorType.UNKNOWN_ERROR))
                }
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            emit(Result.Error(ErrorType.UNKNOWN_ERROR))
        }
    }

    override suspend fun unsubscribeUsers() {
        runCatching {
            websocketUsersClient.close()
            _usersStateFlow.update { null }
        }.onFailure { error ->
            if (BuildConfig.DEBUG) {
                error.printStackTrace()
            }
        }
    }

    override suspend fun subscribeSessionResult(sessionId: String) {
        repositoryScope.launch {
            subscribeSessionResultFlow(sessionId).collect { result ->
                _sessionResultFlow.update { result }
            }
        }
    }

    private fun subscribeSessionResultFlow(sessionId: String): Flow<Result<Session, ErrorType>?> =
        flow {
            @Suppress("TooGenericExceptionCaught")
            try {
                websocketSessionResultClient.subscribe(deviceId, "$sessionId$PATH_SESSION_RESULT")
                    .collect { sessionResult ->
                        if (sessionResult != null) {
                            val session = sessionResult.toDomain()
                            sessionsHistoryRepository.saveSessionsHistory(session)
                            emit(Result.Success(session))
                        } else {
                            emit(Result.Error(ErrorType.UNKNOWN_ERROR))
                        }
                    }
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                emit(Result.Error(ErrorType.UNKNOWN_ERROR))
            }
        }

    override suspend fun unsubscribeSessionResult() {
        runCatching {
            websocketSessionResultClient.close()
            _sessionResultFlow.update { null }
        }.onFailure { error ->
            if (BuildConfig.DEBUG) {
                error.printStackTrace()
            }
        }
    }

    override suspend fun subscribeSessionStatus(sessionId: String) {
        repositoryScope.launch {
            subscribeSessionStatusFlow(sessionId).collect { result ->
                _sessionStatusStateFlow.value = result
            }
        }
    }

    private fun subscribeSessionStatusFlow(sessionId: String): Flow<Result<SessionStatus, ErrorType>?> =
        flow {
            @Suppress("TooGenericExceptionCaught")
            try {
                websocketSessionStatusClient.subscribe(deviceId, "$sessionId$PATH_SESSION_STATUS")
                    .collect { sessionStatus ->
                        if (sessionStatus != null) {
                            emit(Result.Success(sessionStatus.toDomain()))
                        } else {
                            emit(Result.Error(ErrorType.UNKNOWN_ERROR))
                        }
                    }
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                emit(Result.Error(ErrorType.UNKNOWN_ERROR))
            }
        }

    override suspend fun unsubscribeSessionStatus() {
        runCatching {
            websocketSessionStatusClient.close()
            _sessionStatusStateFlow.update { null }
        }.onFailure { error ->
            if (BuildConfig.DEBUG) {
                error.printStackTrace()
            }
        }
    }

    override suspend fun subscribeRouletteId(sessionId: String) {
        repositoryScope.launch {
            subscribeRouletteIdFlow(sessionId).collect { result ->
                _rouletteIdStateFlow.update { result }
            }
        }
    }

    private fun subscribeRouletteIdFlow(sessionId: String): Flow<Result<Int, ErrorType>?> =
        flow {
            @Suppress("TooGenericExceptionCaught")
            try {
                websocketRouletteIdClient.subscribe(deviceId, "$sessionId$PATH_ROULETTE")
                    .collect { id ->
                        if (id != null) {
                            emit(Result.Success(id))
                        } else {
                            emit(Result.Error(ErrorType.UNKNOWN_ERROR))
                        }
                    }
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                emit(Result.Error(ErrorType.UNKNOWN_ERROR))
            }
        }

    override suspend fun unsubscribeRouletteId() {
        runCatching {
            _rouletteIdStateFlow.update { null }
            websocketRouletteIdClient.close()
        }.onFailure { error ->
            if (BuildConfig.DEBUG) {
                error.printStackTrace()
            }
        }
    }

    override suspend fun subscribeMatchesId(sessionId: String) {
        repositoryScope.launch {
            subscribeMatchesIdFlow(sessionId).collect { result ->
                _matchesIdStateFlow.update { result }
            }
        }
    }

    private fun subscribeMatchesIdFlow(sessionId: String): Flow<Result<Int, ErrorType>?> =
        flow {
            @Suppress("TooGenericExceptionCaught")
            try {
                websocketMatchesIdClient.subscribe(deviceId, "$sessionId$PATH_MATCHES")
                    .collect { id ->
                        if (id != null) {
                            emit(Result.Success(id))
                        } else {
                            emit(Result.Error(ErrorType.UNKNOWN_ERROR))
                        }
                    }
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                emit(Result.Error(ErrorType.UNKNOWN_ERROR))
            }
        }

    override suspend fun unsubscribeMatchesId() {
        runCatching {
            websocketMatchesIdClient.close()
            _matchesIdStateFlow.update { null }
        }.onFailure { error ->
            if (BuildConfig.DEBUG) {
                error.printStackTrace()
            }
        }
    }
}