package com.davay.android.feature.roulette.data.network

import android.content.Context
import com.davay.android.core.data.network.HttpKtorNetworkClient
import com.davay.android.core.data.network.model.NetworkParams
import com.davay.android.feature.roulette.data.network.model.StartRouletteRequest
import com.davay.android.feature.roulette.data.network.model.StartRouletteResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.headers
import io.ktor.http.path
import javax.inject.Inject

class StartRouletteKtorNetworkClient @Inject constructor(
    context: Context,
    private val httpClient: HttpClient
) : HttpKtorNetworkClient<StartRouletteRequest, StartRouletteResponse>(context) {

    override suspend fun getResponseBodyByRequestType(
        requestType: StartRouletteRequest,
        httpResponse: HttpResponse
    ): StartRouletteResponse {
        return StartRouletteResponse(httpResponse.body())
    }

    override suspend fun sendRequestByType(request: StartRouletteRequest): HttpResponse {
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