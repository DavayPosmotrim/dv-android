package com.davay.android.feature.roulette.domain.api

import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import kotlinx.coroutines.flow.Flow

interface StartRouletteRepository {
    fun startRoulette(sessionId: String): Flow<Result<Int, ErrorType>>
}