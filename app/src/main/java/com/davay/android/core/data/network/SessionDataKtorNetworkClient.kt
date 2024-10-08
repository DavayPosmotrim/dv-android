package com.davay.android.core.data.network

import android.content.Context
import com.davay.android.core.data.network.model.NetworkParams
import com.davay.android.core.data.network.model.SessionDataRequest
import com.davay.android.core.data.network.model.SessionDataResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path
import javax.inject.Inject

class SessionDataKtorNetworkClient @Inject constructor(
    context: Context,
    private val httpClient: HttpClient
) : HttpKtorNetworkClient<SessionDataRequest, SessionDataResponse>(context) {

    override suspend fun getResponseBodyByRequestType(
        requestType: SessionDataRequest,
        httpResponse: HttpResponse
    ): SessionDataResponse {
        return SessionDataResponse(httpResponse.body())
    }

    override suspend fun sendRequestByType(request: SessionDataRequest): HttpResponse {
        return httpClient.get {
            url {
                path(request.path)
            }

            headers {
                append(NetworkParams.DEVICE_ID_HEADER, request.userId)
            }
        }
    }
}
