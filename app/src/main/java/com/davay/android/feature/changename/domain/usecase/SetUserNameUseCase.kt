package com.davay.android.feature.changename.domain.usecase

import com.davay.android.core.domain.api.NetworkUserDataRepository
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetUserNameUseCase @Inject constructor(
    private val repository: NetworkUserDataRepository
) {
    fun execute(userName: String): Flow<Result<Unit, ErrorType>> {
        return repository.setUserData(userName)
    }
}