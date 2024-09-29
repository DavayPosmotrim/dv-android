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
import com.davay.android.core.domain.models.NoInternetConnectionException
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
    userDataRepository: UserDataRepository
) : WebsocketRepository {

    private val deviceId = userDataRepository.getUserId()
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _usersStateFlow = MutableStateFlow<Result<List<User>, ErrorType>?>(null)
    override val usersStateFlow: StateFlow<Result<List<User>, ErrorType>?> get() = _usersStateFlow
    private var isUsersSubscribed = false

    private val _sessionResultFlow = MutableStateFlow<Result<Session, ErrorType>?>(null)
    override val sessionResultFlow: StateFlow<Result<Session, ErrorType>?> get() = _sessionResultFlow
    private var isSessionResultSubscribed = false

    private val _sessionStatusStateFlow = MutableStateFlow<Result<SessionStatus, ErrorType>?>(null)
    override val sessionStatusStateFlow: StateFlow<Result<SessionStatus, ErrorType>?> get() = _sessionStatusStateFlow
    private var isSessionStatusSubscribed = false

    private val _rouletteIdStateFlow = MutableStateFlow<Result<Int, ErrorType>?>(null)
    override val rouletteIdStateFlow: StateFlow<Result<Int, ErrorType>?> get() = _rouletteIdStateFlow
    private var isRouletteIdSubscribed = false

    private val _matchesIdStateFlow = MutableStateFlow<Result<Int, ErrorType>?>(null)
    override val matchesIdStateFlow: StateFlow<Result<Int, ErrorType>?> get() = _matchesIdStateFlow
    private var isMatchesIdSubscribed = false

    override fun subscribeUsers(sessionId: String): StateFlow<Result<List<User>, ErrorType>?> {
        if (!isUsersSubscribed) {
            isUsersSubscribed = true
            repositoryScope.launch {
                subscribeUsersFlow(sessionId).collect { result ->
                    _usersStateFlow.value = result
                }
            }
        }
        return usersStateFlow
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
        } catch (e: NoInternetConnectionException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            emit(Result.Error(ErrorType.NO_CONNECTION))
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            emit(Result.Error(ErrorType.UNKNOWN_ERROR))
        }
    }

    override suspend fun unsubscribeUsers() {
        runCatching {
            isUsersSubscribed = false
            websocketUsersClient.close()
        }.onFailure { error ->
            if (BuildConfig.DEBUG) {
                error.printStackTrace()
            }
        }
    }

    override fun subscribeSessionResult(sessionId: String): StateFlow<Result<Session, ErrorType>?> {
        if (!isSessionResultSubscribed) {
            isSessionResultSubscribed = true
            repositoryScope.launch {
                subscribeSessionResultFlow(sessionId).collect { result ->
                    _sessionResultFlow.value = result
                }
            }
        }
        return sessionResultFlow
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
            } catch (e: NoInternetConnectionException) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                emit(Result.Error(ErrorType.NO_CONNECTION))
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                emit(Result.Error(ErrorType.UNKNOWN_ERROR))
            }
        }

    override suspend fun unsubscribeSessionResult() {
        runCatching {
            isSessionResultSubscribed = false
            websocketSessionResultClient.close()
        }.onFailure { error ->
            if (BuildConfig.DEBUG) {
                error.printStackTrace()
            }
        }
    }

    override fun subscribeSessionStatus(sessionId: String): StateFlow<Result<SessionStatus, ErrorType>?> {
        if (!isSessionStatusSubscribed) {
            isSessionStatusSubscribed = true
            repositoryScope.launch {
                subscribeSessionStatusFlow(sessionId).collect { result ->
                    _sessionStatusStateFlow.value = result
                }
            }
        }
        return sessionStatusStateFlow
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
            } catch (e: NoInternetConnectionException) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                emit(Result.Error(ErrorType.NO_CONNECTION))
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                emit(Result.Error(ErrorType.UNKNOWN_ERROR))
            }
        }

    override suspend fun unsubscribeSessionStatus() {
        runCatching {
            isSessionStatusSubscribed = false
            websocketSessionStatusClient.close()
        }.onFailure { error ->
            if (BuildConfig.DEBUG) {
                error.printStackTrace()
            }
        }
    }

    override fun subscribeRouletteId(sessionId: String): StateFlow<Result<Int, ErrorType>?> {
        if (!isRouletteIdSubscribed) {
            isRouletteIdSubscribed = true
            repositoryScope.launch {
                subscribeRouletteIdFlow(sessionId).collect { result ->
                    _rouletteIdStateFlow.value = result
                }
            }
        }
        return rouletteIdStateFlow
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
            } catch (e: NoInternetConnectionException) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                emit(Result.Error(ErrorType.NO_CONNECTION))
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                emit(Result.Error(ErrorType.UNKNOWN_ERROR))
            }
        }

    override suspend fun unsubscribeRouletteId() {
        runCatching {
            isRouletteIdSubscribed = false
            websocketRouletteIdClient.close()
        }.onFailure { error ->
            if (BuildConfig.DEBUG) {
                error.printStackTrace()
            }
        }
    }

    override fun subscribeMatchesId(sessionId: String): StateFlow<Result<Int, ErrorType>?> {
        if (!isMatchesIdSubscribed) {
            isMatchesIdSubscribed = true
            repositoryScope.launch {
                subscribeMatchesIdFlow(sessionId).collect { result ->
                    _matchesIdStateFlow.value = result
                }
            }
        }
        return matchesIdStateFlow
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
            } catch (e: NoInternetConnectionException) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                emit(Result.Error(ErrorType.NO_CONNECTION))
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                emit(Result.Error(ErrorType.UNKNOWN_ERROR))
            }
        }

    override suspend fun unsubscribeMatchesId() {
        runCatching {
            isMatchesIdSubscribed = false
            websocketMatchesIdClient.close()
        }.onFailure { error ->
            if (BuildConfig.DEBUG) {
                error.printStackTrace()
            }
        }
    }
}