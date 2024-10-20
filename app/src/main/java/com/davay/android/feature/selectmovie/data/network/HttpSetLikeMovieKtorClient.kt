package com.davay.android.feature.selectmovie.data.network

import android.content.Context
import android.util.Log
import com.davay.android.BuildConfig
import com.davay.android.core.data.network.HttpKtorNetworkClient
import com.davay.android.core.data.network.model.NetworkParams
import com.davay.android.feature.selectmovie.data.network.models.LikeMovieRequest
import com.davay.android.feature.selectmovie.data.network.models.LikeMovieResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path
import javax.inject.Inject

class HttpSetLikeMovieKtorClient @Inject constructor(
    context: Context,
    private val httpClient: HttpClient
) : HttpKtorNetworkClient<LikeMovieRequest, LikeMovieResponse>(context) {
    override suspend fun sendRequestByType(request: LikeMovieRequest): HttpResponse {
        return when (request) {
            is LikeMovieRequest.Like -> {
                if (BuildConfig.DEBUG) {
                    Log.v(
                        TAG,
                        "request -> $request"
                    )
                }

                httpClient.post {
                    url {
                        path(request.path)
                    }
                    headers {
                        append(NetworkParams.DEVICE_ID_HEADER, request.userId)
                    }
                }
            }
            is LikeMovieRequest.Dislike -> {
                if (BuildConfig.DEBUG) {
                    Log.v(
                        TAG,
                        "request -> $request"
                    )
                }

                httpClient.delete {
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
        requestType: LikeMovieRequest,
        httpResponse: HttpResponse
    ): LikeMovieResponse {
        return LikeMovieResponse(httpResponse.body())
    }

    private companion object {
        val TAG = HttpSetLikeMovieKtorClient::class.simpleName
    }
}