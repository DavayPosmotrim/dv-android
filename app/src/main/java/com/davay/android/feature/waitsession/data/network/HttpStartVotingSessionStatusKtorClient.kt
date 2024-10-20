package com.davay.android.feature.waitsession.data.network

import android.content.Context
import com.davay.android.core.data.network.HttpKtorNetworkClient
import com.davay.android.core.data.network.model.NetworkParams
import com.davay.android.feature.waitsession.data.network.model.StartVotingSessionRequest
import com.davay.android.feature.waitsession.data.network.model.StartVotingSessionResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path
import javax.inject.Inject

class HttpStartVotingSessionStatusKtorClient @Inject constructor(
    context: Context,
    private val httpClient: HttpClient
) : HttpKtorNetworkClient<StartVotingSessionRequest, StartVotingSessionResponse>(context) {
    override suspend fun sendRequestByType(request: StartVotingSessionRequest): HttpResponse {
        return httpClient.get {
            url {
                path(request.path)
            }
            headers {
                headers {
                    append(NetworkParams.DEVICE_ID_HEADER, request.userId)
                }
            }
        }
    }

    override suspend fun getResponseBodyByRequestType(
        requestType: StartVotingSessionRequest,
        httpResponse: HttpResponse
    ): StartVotingSessionResponse {
        return StartVotingSessionResponse(httpResponse.body())
    }
}