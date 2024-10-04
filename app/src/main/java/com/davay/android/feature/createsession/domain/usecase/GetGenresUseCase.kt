package com.davay.android.feature.createsession.domain.usecase

import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Genre
import com.davay.android.core.domain.models.Result
import com.davay.android.feature.createsession.domain.api.CreateSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGenresUseCase @Inject constructor(
    private val createSessionRepository: CreateSessionRepository
) {
    operator fun invoke(): Flow<Result<List<Genre>, ErrorType>> {
        return createSessionRepository.getGenres()
    }
}