package com.davay.android.core.domain.impl

import com.davay.android.core.domain.api.GetMatchesRepository
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.core.domain.models.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMatchesUseCase @Inject constructor(
    private val repository: GetMatchesRepository,
    private val commonWebsocketInteractor: CommonWebsocketInteractor
) {
    operator fun invoke(): Flow<Result<List<MovieDetails>, ErrorType>> {
        val sessionId = commonWebsocketInteractor.sessionId
        return repository.getMatches(sessionId)
    }
}