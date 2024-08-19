package com.davay.android.feature.changename.domain.api

import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import kotlinx.coroutines.flow.Flow

interface ChangeNameRepository {
    fun setUserName(userName: String): Flow<Result<Unit, ErrorType>>
}