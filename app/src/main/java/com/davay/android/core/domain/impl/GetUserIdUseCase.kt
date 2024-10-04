package com.davay.android.core.domain.impl

import com.davay.android.core.domain.api.UserDataRepository

class GetUserIdUseCase(
    private val repositoryImpl: UserDataRepository
) {
    operator fun invoke(): String {
        return repositoryImpl.getUserId()
    }
}