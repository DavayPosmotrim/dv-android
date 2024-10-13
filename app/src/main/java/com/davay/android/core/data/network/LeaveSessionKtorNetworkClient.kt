package com.davay.android.core.data.network

import android.content.Context
import com.davay.android.core.data.network.model.LeaveSessionRequest
import com.davay.android.core.data.network.model.LeaveSessionResponse
import com.davay.android.core.data.network.model.NetworkParams
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path
import javax.inject.Inject

class LeaveSessionKtorNetworkClient @Inject constructor(
    context: Context,
    private val httpClient: HttpClient
) : HttpKtorNetworkClient<LeaveSessionRequest, LeaveSessionResponse>(context) {

    override suspend fun getResponseBodyByRequestType(
        requestType: LeaveSessionRequest,
        httpResponse: HttpResponse
    ): LeaveSessionResponse {
        return LeaveSessionResponse(httpResponse.body())
    }

    override suspend fun sendRequestByType(request: LeaveSessionRequest): HttpResponse {
        return httpClient.delete {
            url {
                path(request.path)
            }

            headers {
                append(NetworkParams.DEVICE_ID_HEADER, request.userId)
            }
        }
    }
}
