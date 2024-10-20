package com.davay.android.feature.sessionlist.data.network

import android.content.Context
import com.davay.android.core.data.network.HttpKtorNetworkClient
import com.davay.android.core.data.network.model.NetworkParams
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path
import javax.inject.Inject

class HttpConnectToSessionKtorClient @Inject constructor(
    context: Context,
    private val httpClient: HttpClient
) : HttpKtorNetworkClient<ConnectToSessionRequest, ConnectToSessionResponse>(context) {

    override suspend fun sendRequestByType(request: ConnectToSessionRequest): HttpResponse {
        return when (request) {
            is ConnectToSessionRequest.ConnectToSession -> {
                httpClient.post {
                    url {
                        path(request.path)
                    }

                    headers {
                        append(NetworkParams.DEVICE_ID_HEADER, request.userId)
                    }
                }
            }

            is ConnectToSessionRequest.Session -> {
                httpClient.get {
                    url {
                        path(request.path)
                    }

                    headers {
                        append(NetworkParams.DEVICE_ID_HEADER, request.userId)
                    }
                }
            }
        }
    }

    override suspend fun getResponseBodyByRequestType(
        requestType: ConnectToSessionRequest,
        httpResponse: HttpResponse
    ): ConnectToSessionResponse {
        return when (requestType) {
            is ConnectToSessionRequest.ConnectToSession -> {
                ConnectToSessionResponse.ConnectToSession(httpResponse.body())
            }

            is ConnectToSessionRequest.Session -> {
                ConnectToSessionResponse.Session(httpResponse.body())
            }
        }
    }
}