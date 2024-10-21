package com.davay.android.feature.roulette.domain.impl

import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import com.davay.android.feature.roulette.domain.api.StartRouletteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StartRouletteUseCase @Inject constructor(
    private val startRouletteRepository: StartRouletteRepository
) {
    fun startRoulette(sessionId: String): Flow<Result<Int, ErrorType>> {
        return startRouletteRepository.startRoulette(sessionId)
    }
}