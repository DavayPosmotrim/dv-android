package com.davay.android.core.data.network

import android.content.Context
import com.davay.android.core.data.network.model.NetworkParams
import com.davay.android.core.data.network.model.UserDataRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path
import javax.inject.Inject

class UserDataKtorNetworkClient @Inject constructor(
    context: Context,
    private val httpClient: HttpClient
) : HttpKtorNetworkClient<UserDataRequest, HttpResponse>(context) {

    override suspend fun sendResponseByType(request: UserDataRequest): HttpResponse {
        when (request) {
            is UserDataRequest.Registration -> {
                return httpClient.post {
                    url.path(NetworkParams.USERS_API_PATH)
                    header(NetworkParams.DEVICE_ID_HEADER, request.userData.userId)
                    contentType(ContentType.Application.Json)
                    setBody(request.userData)
                }
            }

            is UserDataRequest.ChangeName -> {
                return httpClient.put {
                    url.path(NetworkParams.USERS_API_PATH)
                    headers.append(NetworkParams.DEVICE_ID_HEADER, request.userData.userId)
                    contentType(ContentType.Application.Json)
                    setBody(mapOf(NetworkParams.USER_NAME_BODY_FIELD to request.userData.name))
                }
            }
        }
    }

    override suspend fun getResponseBodyByRequestType(
        requestType: UserDataRequest,
        httpResponse: HttpResponse
    ): HttpResponse {
        return httpResponse
    }
}