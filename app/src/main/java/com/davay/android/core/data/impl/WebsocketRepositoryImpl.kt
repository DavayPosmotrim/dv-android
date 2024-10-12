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
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("LargeClass")
class WebsocketRepositoryImpl @Inject constructor(
    private val websocketUsersClient: WebsocketNetworkClient<List<UserDto>?>,
    private val websocketSessionResultClient: WebsocketNetworkClient<SessionResultDto?>,
    private val websocketSessionStatusClient: WebsocketNetworkClient<SessionStatusDto?>,
    @RouletteIdClient private val websocketRouletteIdClient: WebsocketNetworkClient<Int?>,
    @MatchesIdClient private val websocketMatchesIdClient: WebsocketNetworkClient<Int?>,
    private val userDataRepository: UserDataRepository,
) : WebsocketRepository {

    private val deviceId = userDataRepository.getUserId()
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _usersStateFlow = MutableStateFlow<Result<List<User>, ErrorType>?>(null)
    override val usersStateFlow: StateFlow<Result<List<User>, ErrorType>?> get() = _usersStateFlow
    private val isUsersSubscribedFlow: StateFlow<Boolean> = websocketUsersClient.connectionState

    private val _sessionResultFlow = MutableStateFlow<Result<Session, ErrorType>?>(null)
    override val sessionResultFlow: StateFlow<Result<Session, ErrorType>?> get() = _sessionResultFlow
    private val isSessionResultSubscribedFlow: StateFlow<Boolean> =
        websocketSessionResultClient.connectionState

    private val _sessionStatusStateFlow = MutableStateFlow<Result<SessionStatus, ErrorType>?>(null)
    override val sessionStatusStateFlow: StateFlow<Result<SessionStatus, ErrorType>?> get() = _sessionStatusStateFlow
    private val isSessionStatusSubscribedFlow: StateFlow<Boolean> =
        websocketSessionStatusClient.connectionState

    private val _rouletteIdStateFlow = MutableStateFlow<Result<Int, ErrorType>?>(null)
    override val rouletteIdStateFlow: StateFlow<Result<Int, ErrorType>?> get() = _rouletteIdStateFlow
    private val isRouletteIdSubscribedFlow: StateFlow<Boolean> =
        websocketRouletteIdClient.connectionState

    private val _matchesIdStateFlow = MutableStateFlow<Result<Int, ErrorType>?>(null)
    override val matchesIdStateFlow: StateFlow<Result<Int, ErrorType>?> get() = _matchesIdStateFlow
    private val isMatchesIdSubscribedFlow: StateFlow<Boolean> =
        websocketMatchesIdClient.connectionState

    override suspend fun subscribeUsers(sessionId: String) {
        if (!isUsersSubscribedFlow.value) {
            repositoryScope.launch {
                subscribeUsersFlow(sessionId).collect { result ->
                    _usersStateFlow.value = result
                }
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
            _usersStateFlow.value = null
        }.onFailure { error ->
            if (BuildConfig.DEBUG) {
                error.printStackTrace()
            }
        }
    }

    override suspend fun subscribeSessionResult(sessionId: String) {
        if (!isSessionResultSubscribedFlow.value) {
            repositoryScope.launch {
                subscribeSessionResultFlow(sessionId).collect { result ->
                    _sessionResultFlow.value = result
                }
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
                            emit(Result.Success(sessionResult.toDomain()))
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
            _sessionResultFlow.value = null
        }.onFailure { error ->
            if (BuildConfig.DEBUG) {
                error.printStackTrace()
            }
        }
    }

    override suspend fun subscribeSessionStatus(sessionId: String) {
        if (!isSessionStatusSubscribedFlow.value) {
            repositoryScope.launch {
                subscribeSessionStatusFlow(sessionId).collect { result ->
                    _sessionStatusStateFlow.value = result
                }
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
            _sessionStatusStateFlow.value = null
            websocketSessionStatusClient.close()
        }.onFailure { error ->
            if (BuildConfig.DEBUG) {
                error.printStackTrace()
            }
        }
    }

    override suspend fun subscribeRouletteId(sessionId: String) {
        if (!isRouletteIdSubscribedFlow.value) {
            repositoryScope.launch {
                subscribeRouletteIdFlow(sessionId).collect { result ->
                    _rouletteIdStateFlow.value = result
                }
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
            _rouletteIdStateFlow.value = null
            websocketRouletteIdClient.close()
        }.onFailure { error ->
            if (BuildConfig.DEBUG) {
                error.printStackTrace()
            }
        }
    }

    override suspend fun subscribeMatchesId(sessionId: String) {
        if (!isMatchesIdSubscribedFlow.value) {
            repositoryScope.launch {
                subscribeMatchesIdFlow(sessionId).collect { result ->
                    _matchesIdStateFlow.value = result
                }
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
            _matchesIdStateFlow.value = null
            websocketMatchesIdClient.close()
        }.onFailure { error ->
            if (BuildConfig.DEBUG) {
                error.printStackTrace()
            }
        }
    }
}