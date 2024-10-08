package com.davay.android.core.data.impl

import com.davay.android.core.data.converters.toDomain
import com.davay.android.core.data.network.HttpNetworkClient
import com.davay.android.core.data.network.model.SessionDataRequest
import com.davay.android.core.data.network.model.SessionDataResponse
import com.davay.android.core.data.network.model.mapToErrorType
import com.davay.android.core.domain.api.SessionDataRepository
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import com.davay.android.core.domain.models.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SessionDataRepositoryImpl @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val httpNetworkClient: HttpNetworkClient<SessionDataRequest, SessionDataResponse>
) : SessionDataRepository {
    override fun getSessionData(sessionId: String): Flow<Result<Session, ErrorType>> = flow {
        val userId = userDataRepository.getUserId()
        val response = httpNetworkClient.getResponse(
            SessionDataRequest(
                sessionId = sessionId,
                userId = userId
            )
        )
        when (val body = response.body) {
            is SessionDataResponse -> {
                emit(Result.Success(body.value.toDomain()))
            }

            else -> {
                emit(Result.Error(response.resultCode.mapToErrorType()))
            }
        }
    }
}