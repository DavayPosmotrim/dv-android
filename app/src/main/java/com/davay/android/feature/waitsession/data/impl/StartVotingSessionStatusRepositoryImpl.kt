package com.davay.android.feature.waitsession.data.impl

import com.davay.android.core.data.network.HttpNetworkClient
import com.davay.android.core.data.network.model.mapToErrorType
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import com.davay.android.feature.waitsession.data.network.model.StartVotingSessionRequest
import com.davay.android.feature.waitsession.data.network.model.StartVotingSessionResponse
import com.davay.android.feature.waitsession.domain.api.StartVotingSessionStatusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StartVotingSessionStatusRepositoryImpl(
    private val userDataRepository: UserDataRepository,
    private val httpClient: HttpNetworkClient<StartVotingSessionRequest, StartVotingSessionResponse>
) : StartVotingSessionStatusRepository {

    override fun startVotingSession(sessionId: String): Flow<Result<String, ErrorType>> =
        flow {
            val deviceId = userDataRepository.getUserId()
            val response = httpClient.getResponse(
                StartVotingSessionRequest(
                    sessionId = sessionId,
                    userId = deviceId
                )
            )
            when (val body = response.body) {
                is StartVotingSessionResponse -> {
                    emit(Result.Success(body.value))
                }

                else -> {
                    emit(Result.Error(response.resultCode.mapToErrorType()))
                }
            }
        }
}