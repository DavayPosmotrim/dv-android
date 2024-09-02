package com.davay.android.core.domain.impl

import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.core.domain.models.UserDataFields
import com.davay.android.core.domain.usecases.SetUserDataUseCase

class SetUserDataUseCaseImpl(
    private val repository: UserDataRepository
) : SetUserDataUseCase {

    override fun setUserData(value: UserDataFields) {
        when (value) {
            is UserDataFields.UserName -> repository.setUserName(value.userName)
            is UserDataFields.UserId -> repository.setUserId(value.userId)
        }
    }
}