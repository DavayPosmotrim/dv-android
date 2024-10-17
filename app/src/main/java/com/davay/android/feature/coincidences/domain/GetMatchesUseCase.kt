package com.davay.android.feature.coincidences.domain

import com.davay.android.core.domain.impl.CommonWebsocketInteractor
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.core.domain.models.Result
import com.davay.android.feature.coincidences.domain.api.CoincidencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMatchesUseCase @Inject constructor(
    private val repository: CoincidencesRepository,
    private val commonWebsocketInteractor: CommonWebsocketInteractor
) {
    operator fun invoke(): Flow<Result<List<MovieDetails>, ErrorType>> {
        val sessionId = commonWebsocketInteractor.sessionId
        return repository.getMatches(sessionId)
    }
}