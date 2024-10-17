package com.davay.android.feature.roulette.data.impl

import com.davay.android.core.data.network.HttpNetworkClient
import com.davay.android.core.data.network.model.mapToErrorType
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import com.davay.android.feature.roulette.data.network.model.StartRouletteRequest
import com.davay.android.feature.roulette.data.network.model.StartRouletteResponse
import com.davay.android.feature.roulette.domain.api.StartRouletteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StartRouletteRepositoryImpl @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val httpNetworkClient: HttpNetworkClient<StartRouletteRequest, StartRouletteResponse>
) : StartRouletteRepository {

    override fun startRoulette(sessionId: String): Flow<Result<Int, ErrorType>> = flow {
        val userId = userDataRepository.getUserId()
        val response = httpNetworkClient.getResponse(
            StartRouletteRequest(
                sessionId = sessionId,
                userId = userId
            )
        )
        when (val body = response.body) {
            is StartRouletteResponse -> {
                emit(Result.Success(body.value.randomMovieId))
            }

            else -> {
                emit(Result.Error(response.resultCode.mapToErrorType()))
            }
        }
    }
}