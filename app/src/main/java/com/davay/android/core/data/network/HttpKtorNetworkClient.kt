package com.davay.android.core.data.network

import android.content.Context
import android.util.Log
import com.davay.android.BuildConfig
import com.davay.android.core.data.network.model.Response
import com.davay.android.core.data.network.model.StatusCode
import com.davay.android.extensions.isInternetReachable
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.utils.io.errors.IOException
import kotlinx.serialization.SerializationException

abstract class HttpKtorNetworkClient<SealedRequest, SealedResponse>(
    private val context: Context
) : HttpNetworkClient<SealedRequest, SealedResponse> {
    override suspend fun getResponse(sealedRequest: SealedRequest): Response<SealedResponse> {
        return if (context.isInternetReachable()) {
            runCatching {
                mapToResponse(
                    requestType = sealedRequest,
                    httpResponse = sendResponseByType(sealedRequest)
                )
            }.onFailure { error ->
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, "error -> ${error.localizedMessage}")
                    error.printStackTrace()
                }
            }.getOrNull() ?: Response()
        } else {
            Response(isSuccess = false, resultCode = StatusCode(-1))
        }
    }

    protected abstract suspend fun sendResponseByType(request: SealedRequest): HttpResponse

    protected abstract suspend fun getResponseBodyByRequestType(
        requestType: SealedRequest,
        httpResponse: HttpResponse
    ): SealedResponse

    private suspend fun mapToResponse(
        requestType: SealedRequest,
        httpResponse: HttpResponse
    ): Response<SealedResponse> {
        Log.v(TAG, "HTTP response status: ${httpResponse.status.value}")
        Log.v(TAG, "body = ${httpResponse.bodyAsText()}")
        return if (httpResponse.status.isSuccess()) {
            try {
                Response(
                    isSuccess = true,
                    resultCode = StatusCode(httpResponse.status.value),
                    body = getResponseBodyByRequestType(requestType, httpResponse)
                )
            } catch (e: SerializationException) {
                Log.v(TAG, e.localizedMessage.toString())
                Response(
                    isSuccess = false,
                    resultCode = StatusCode(httpResponse.status.value)
                )
            } catch (e: IOException) {
                Log.v(TAG, e.localizedMessage.toString())
                Response(
                    isSuccess = false,
                    resultCode = StatusCode(httpResponse.status.value)
                )
            }
        } else {
            Response(
                isSuccess = false,
                resultCode = StatusCode(httpResponse.status.value)
            )
        }
    }

    private companion object {
        val TAG = HttpKtorNetworkClient::class.simpleName ?: "HttpKtorNetworkClient"
    }
}