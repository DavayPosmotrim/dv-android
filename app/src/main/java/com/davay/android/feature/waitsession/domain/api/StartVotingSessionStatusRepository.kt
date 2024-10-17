package com.davay.android.feature.waitsession.domain.api

import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import kotlinx.coroutines.flow.Flow

interface StartVotingSessionStatusRepository {
    fun startVotingSession(sessionId: String): Flow<Result<String, ErrorType>>
}