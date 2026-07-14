/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.network.http

import com.nextcloud.android.common.ui.BuildConfig
import com.nextcloud.android.common.ui.network.auth.AuthInterceptor
import com.nextcloud.android.common.ui.network.auth.ServerCredentials
import com.nextcloud.android.common.ui.network.model.Meta
import com.nextcloud.android.common.ui.network.model.NetworkResult
import com.nextcloud.android.common.ui.network.model.Ocs
import com.nextcloud.android.common.ui.network.model.OcsResponse
import com.nextcloud.android.common.ui.network.serialization.OCSSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import java.util.concurrent.TimeUnit

class NextcloudHttpClient private constructor(
    val okHttpClient: OkHttpClient,
    val credentials: ServerCredentials,
    private val debugLogger: NextcloudHttpClientLogger
) {
    companion object {
        private const val CONNECT_TIMEOUT_SECONDS = 90L
        private const val READ_TIMEOUT_SECONDS = 90L
        private const val WRITE_TIMEOUT_SECONDS = 90L
        private const val OCS_OK = "ok"
        private const val OCS_FAILURE = "failure"

        fun create(
            credentials: ServerCredentials
        ): NextcloudHttpClient {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(AuthInterceptor(credentials))
                .build()

            return NextcloudHttpClient(okHttpClient, credentials, NextcloudHttpClientLogger(BuildConfig.DEBUG))
        }
    }

    private val baseUrl get() = credentials.baseURL.trimEnd('/')

    private fun errorMeta(code: Int): Meta = Meta(status = OCS_FAILURE, statusCode = code, message = "")

    suspend fun <T> executeRequest(
        endpoint: String,
        method: HttpMethod,
        body: RequestBody? = null,
        parse: (String) -> T
    ): NetworkResult<T> = withContext(Dispatchers.IO) {
        val request = buildOcsRequest("$baseUrl$endpoint", method, body)
        debugLogger.logRequest(endpoint, method, body)
        try {
            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body.string()

            if (responseBody.isBlank()) {
                val isError = !response.isSuccessful
                debugLogger.logResponse(endpoint, method, response.code, responseBody, isError = isError)
                return@withContext if (isError) {
                    NetworkResult.ServerError(OcsResponse(Ocs(errorMeta(response.code), "")))
                } else {
                    NetworkResult.Success(parse(responseBody))
                }
            }

            val ocs = OCSSerializer.json
                .decodeFromString<OcsResponse<JsonElement>>(responseBody)
                .ocs
            val meta = ocs.meta
            val isError = !response.isSuccessful || meta.status != OCS_OK
            debugLogger.logResponse(endpoint, method, response.code, responseBody, isError = isError)

            if (isError) {
                val dataMessage = (ocs.data as? JsonPrimitive)?.takeIf { it.isString }?.content
                val message = dataMessage?.takeIf { it.isNotBlank() } ?: meta.message
                NetworkResult.ServerError(OcsResponse(Ocs(meta, message)))
            } else {
                NetworkResult.Success(parse(responseBody))
            }
        } catch (e: Exception) {
            debugLogger.logException(endpoint, method, e)
            NetworkResult.fromException(e)
        }
    }
}
