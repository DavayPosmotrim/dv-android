package com.davay.android.feature.sessionlist.data

import com.davay.android.core.data.MovieIdListToDbSaver
import com.davay.android.core.data.converters.toDomain
import com.davay.android.core.data.database.MovieIdDao
import com.davay.android.core.data.network.HttpNetworkClient
import com.davay.android.core.data.network.model.mapToErrorType
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import com.davay.android.core.domain.models.Session
import com.davay.android.feature.sessionlist.data.network.ConnectToSessionRequest
import com.davay.android.feature.sessionlist.data.network.ConnectToSessionResponse
import com.davay.android.feature.sessionlist.domain.api.ConnectToSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ConnectToSessionRepositoryImpl @Inject constructor(
    private val movieIdDao: MovieIdDao,
    private val userDataRepository: UserDataRepository,
    private val httpNetworkClient: HttpNetworkClient<ConnectToSessionRequest, ConnectToSessionResponse>,
    private val movieIdListToDbSaver: MovieIdListToDbSaver,
) : ConnectToSessionRepository {

    override fun connectToSession(sessionId: String): Flow<Result<Session, ErrorType>> = flow {
        val userId = userDataRepository.getUserId()
        val response = httpNetworkClient.getResponse(
            ConnectToSessionRequest.ConnectToSession(
                sessionId = sessionId,
                userId = userId
            )
        )
        when (response.body) {
            is ConnectToSessionResponse.ConnectToSession -> {
                emitAll(getSession(sessionId, userId))
            }

            else -> {
                emit(Result.Error(response.resultCode.mapToErrorType()))
            }
        }
    }

    private fun getSession(sessionId: String, userId: String): Flow<Result<Session, ErrorType>> =
        flow {
            val response = httpNetworkClient.getResponse(
                ConnectToSessionRequest.Session(
                    sessionId = sessionId,
                    userId = userId
                )
            )
            when (val body = response.body) {
                is ConnectToSessionResponse.Session -> {
                    movieIdListToDbSaver.saveMovieIdListToDb(body.value.movieIdList, movieIdDao)
                    emit(Result.Success(body.value.toDomain()))
                }

                else -> {
                    emit(Result.Error(response.resultCode.mapToErrorType()))
                }
            }
        }
}
