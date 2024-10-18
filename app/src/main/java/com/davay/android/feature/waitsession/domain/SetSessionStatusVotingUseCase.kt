package com.davay.android.feature.waitsession.domain

import com.davay.android.core.domain.impl.CommonWebsocketInteractor
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import com.davay.android.feature.waitsession.domain.api.StartVotingSessionStatusRepository
import kotlinx.coroutines.flow.Flow

class SetSessionStatusVotingUseCase(
    private val commonWebsocketInteractor: CommonWebsocketInteractor,
    private val startVotingRepository: StartVotingSessionStatusRepository
) {

    operator fun invoke(): Flow<Result<String, ErrorType>> {
        val sessionId = commonWebsocketInteractor.sessionId

        return startVotingRepository.startVotingSession(sessionId)
    }
}