package com.davay.android.core.data.network

import android.content.Context
import com.davay.android.core.data.network.model.NetworkParams
import com.davay.android.core.data.network.model.getmatches.GetMatchesRequest
import com.davay.android.core.data.network.model.getmatches.GetMatchesResponse
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
) : HttpKtorNetworkClient<GetMatchesRequest, GetMatchesResponse>(context) {
    override suspend fun sendRequestByType(request: GetMatchesRequest): HttpResponse {
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
        requestType: GetMatchesRequest,
        httpResponse: HttpResponse
    ): GetMatchesResponse {
        return GetMatchesResponse.Session(httpResponse.body())
    }
}