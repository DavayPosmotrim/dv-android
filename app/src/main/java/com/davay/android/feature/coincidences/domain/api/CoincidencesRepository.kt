package com.davay.android.feature.coincidences.domain.api

import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.MovieDetails
import com.davay.android.core.domain.models.Result
import kotlinx.coroutines.flow.Flow

interface CoincidencesRepository {
    fun getMatches(sessionId: String): Flow<Result<List<MovieDetails>, ErrorType>>
}