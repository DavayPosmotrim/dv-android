package com.davay.android.core.domain.api

import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.core.domain.models.Result
import kotlinx.coroutines.flow.Flow

interface GetMatchesRepository {
    fun getMatches(sessionId: String): Flow<Result<List<MovieDetails>, ErrorType>>
}