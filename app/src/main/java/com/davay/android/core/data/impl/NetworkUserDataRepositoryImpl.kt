package com.davay.android.core.data.impl

import com.davay.android.core.data.dto.UserDto
import com.davay.android.core.data.network.HttpNetworkClient
import com.davay.android.core.data.network.model.UserDataRequest
import com.davay.android.core.data.network.model.mapToErrorType
import com.davay.android.core.domain.api.NetworkUserDataRepository
import com.davay.android.core.domain.api.UserDataRepository
import com.davay.android.core.domain.models.ErrorType
import com.davay.android.core.domain.models.Result
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID.randomUUID

class NetworkUserDataRepositoryImpl(
    private val httpNetworkClient: HttpNetworkClient<UserDataRequest, HttpResponse>,
    private val userDataRepository: UserDataRepository
) : NetworkUserDataRepository {
    private var isRegistration = false
    private var userId = userDataRepository.getUserId()

    override fun setUserData(userName: String): Flow<Result<Unit, ErrorType>> = flow {
        val userData = UserDto(userId = getUserId(), name = userName)
        val response = getResponse(userData)

        if (response.isSuccess) {
            userDataRepository.setUserName(userName)
            emit(Result.Success(Unit))
        } else {
            emit(Result.Error(response.resultCode.mapToErrorType()))
        }
    }

    private fun getUserId() =
        userId.ifEmpty {
            isRegistration = true
            userId = randomUUID().toString()
            userDataRepository.setUserId(userId)
            userId
        }

    private suspend fun getResponse(userData: UserDto) =
        httpNetworkClient.getResponse(
            if (isRegistration) {
                UserDataRequest.Registration(userData)
            } else {
                UserDataRequest.ChangeName(userData)
            }
        )
}