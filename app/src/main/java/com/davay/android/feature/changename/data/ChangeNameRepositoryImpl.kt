package com.davay.android.feature.changename.data

import com.davay.android.core.data.dto.UserDto
import com.davay.android.core.data.network.HttpNetworkClient
import com.davay.android.core.data.network.model.mapToErrorType
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import com.davay.android.core.domain.models.User
import com.davay.android.feature.changename.data.network.ChangeNameRequest
import com.davay.android.feature.changename.data.network.ChangeNameResponse
import com.davay.android.feature.changename.domain.api.ChangeNameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChangeNameRepositoryImpl(
    private val httpNetworkClient: HttpNetworkClient<ChangeNameRequest, ChangeNameResponse>,
    private val userDataRepository: UserDataRepository
) : ChangeNameRepository {

    override fun setUserName(userData: User): Flow<Result<UserDto, ErrorType>> = flow {
        val response = httpNetworkClient.getResponse(ChangeNameRequest(userData = userData))
        when (val body = response.body) {
            is ChangeNameResponse -> {
                userDataRepository.setUserName(body.userData.name)
                emit(Result.Success(body.userData))
            }
            else -> {
                emit(Result.Error(response.resultCode.mapToErrorType()))
            }
        }
    }
}