package com.davay.android.core.data.impl

import com.davay.android.core.data.network.HttpNetworkClient
import com.davay.android.core.data.network.model.LeaveSessionRequest
import com.davay.android.core.data.network.model.LeaveSessionResponse
import com.davay.android.core.data.network.model.mapToErrorType
import com.davay.android.core.domain.api.LeaveSessionRepository
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LeaveSessionRepositoryImpl @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val httpNetworkClient: HttpNetworkClient<LeaveSessionRequest, LeaveSessionResponse>
) : LeaveSessionRepository {
    override fun leaveSession(sessionId: String): Flow<Result<String, ErrorType>> = flow {
        val userId = userDataRepository.getUserId()
        val response = httpNetworkClient.getResponse(
            LeaveSessionRequest(
                sessionId = sessionId,
                userId = userId
            )
        )
        when (val body = response.body) {
            is LeaveSessionResponse -> {
                emit(Result.Success(body.value.message))
            }

            else -> {
                emit(Result.Error(response.resultCode.mapToErrorType()))
            }
        }
    }
}