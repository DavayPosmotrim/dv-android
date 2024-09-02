package com.davay.android.feature.registration.domain.usecase

import com.davay.android.core.domain.api.NetworkUserDataRepository
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegistrationUseCase @Inject constructor(
    private val registrationRepository: NetworkUserDataRepository
) {
    fun execute(userName: String): Flow<Result<Unit, ErrorType>> {
        return registrationRepository.setUserData(userName)
    }
}