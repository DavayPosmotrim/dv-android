package com.davay.android.core.data.impl

import android.database.sqlite.SQLiteException
import android.util.Log
import com.davay.android.BuildConfig
import com.davay.android.core.data.converters.toDomain
import com.davay.android.core.data.network.HttpNetworkClient
import com.davay.android.core.data.network.model.getsession.GetSessionRequest
import com.davay.android.core.data.network.model.getsession.GetSessionResponse
import com.davay.android.core.data.network.model.mapToErrorType
import com.davay.android.core.domain.api.GetSessionRepository
import com.davay.android.core.domain.api.SessionsHistoryRepository
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import com.davay.android.core.domain.models.Session
import com.davay.android.utils.SorterList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetSessionRepositoryIml @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val httpNetworkClient: HttpNetworkClient<GetSessionRequest, GetSessionResponse>,
    private val sessionsHistoryRepository: SessionsHistoryRepository,
    private val sorterList: SorterList
) : GetSessionRepository {

    /**
     * Метод производит запрос на сервер для получения данных о сессии, сортирует список юзеров
     * и если список мэтчей не пустой, то вызывает сохранение сессии в БД
     */
    override fun getSessionAndSaveToDb(sessionId: String): Flow<Result<Session, ErrorType>> = flow {
        val deviceId = userDataRepository.getUserId()
        val response = httpNetworkClient.getResponse(
            GetSessionRequest(
                sessionId = sessionId,
                userId = deviceId
            )
        )
        when (val body = response.body) {
            is GetSessionResponse.Session -> {
                val sessionResult = body.value
                val sessionDomain = sessionResult.toDomain()
                val userName = userDataRepository.getUserName()
                val session = sessionDomain.copy(
                    users = sorterList.sortStringUserList(
                        sessionDomain.users,
                        userName
                    )
                )
                if (sessionResult.matchedMovies.isEmpty()) {
                    if (BuildConfig.DEBUG) {
                        Log.i(TAG, "session.matchedMovies is empty")
                    }
                } else {
                    saveSessionToDb(session)
                }
                emit(Result.Success(session))
            }

            else -> {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "getMatches response body: ${response.body}")
                }
                emit(Result.Error(response.resultCode.mapToErrorType()))
            }
        }
    }

    private suspend fun saveSessionToDb(session: Session) {
        try {
            sessionsHistoryRepository.saveSessionsHistory(session)
        } catch (e: SQLiteException) {
            @Suppress("MaxLineLength")
            if (BuildConfig.DEBUG) {
                Log.e(
                    TAG,
                    "saveSessionToDb for session id: ${session.id} matched movies id: ${session.matchedMovieIdList} error -> ${e.localizedMessage}"
                )
            }
        }
    }

    private companion object {
        val TAG = GetSessionRepositoryIml::class.simpleName
    }
}