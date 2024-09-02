package com.davay.android.core.domain.api

import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import kotlinx.coroutines.flow.Flow

interface NetworkUserDataRepository {
    fun setUserData(userName: String): Flow<Result<Unit, ErrorType>>
}