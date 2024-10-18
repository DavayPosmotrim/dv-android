package com.davay.android.feature.coincidences.data.network

import android.content.Context
import com.davay.android.core.data.network.HttpKtorNetworkClient
import com.davay.android.core.data.network.model.NetworkParams
import com.davay.android.feature.coincidences.data.network.models.GetSessionRequest
import com.davay.android.feature.coincidences.data.network.models.GetSessionResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path
import javax.inject.Inject

class HttpGetMatchesKtorClient @Inject constructor(
    context: Context,
    private val httpClient: HttpClient
) : HttpKtorNetworkClient<GetSessionRequest, GetSessionResponse>(context) {
    override suspend fun sendRequestByType(request: GetSessionRequest): HttpResponse {
        return httpClient.get {
            url {
                path(request.path)
            }

            headers {
                append(NetworkParams.DEVICE_ID_HEADER, request.userId)
            }
        }
    }

    override suspend fun getResponseBodyByRequestType(
        requestType: GetSessionRequest,
        httpResponse: HttpResponse
    ): GetSessionResponse {
        return GetSessionResponse.Session(httpResponse.body())
    }
}