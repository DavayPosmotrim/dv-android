package com.davay.android.feature.selectmovie.data.network

import android.content.Context
import com.davay.android.core.data.network.HttpKtorNetworkClient
import com.davay.android.feature.selectmovie.data.network.models.GetMovieRequest
import com.davay.android.feature.selectmovie.data.network.models.GetMovieResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path
import javax.inject.Inject

class HttpGetMovieDetailsKtorClient @Inject constructor(
    context: Context,
    private val httpClient: HttpClient
) : HttpKtorNetworkClient<GetMovieRequest, GetMovieResponse>(context) {
    override suspend fun sendRequestByType(request: GetMovieRequest): HttpResponse {
        return httpClient.get {
            url {
                path(request.path)
            }
        }
    }

    override suspend fun getResponseBodyByRequestType(
        requestType: GetMovieRequest,
        httpResponse: HttpResponse
    ): GetMovieResponse {
        return GetMovieResponse.Movie(httpResponse.body())
    }
}