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
import com.nextcloud.android.common.ui.network.model.NetworkResult
import com.nextcloud.android.common.ui.network.model.Ocs
import com.nextcloud.android.common.ui.network.model.OcsResponse
import com.nextcloud.android.common.ui.network.serialization.OCSSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
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

    suspend fun <T> executeRequest(
        endpoint: String,
        method: HttpMethod,
        body: RequestBody? = null,
        parse: (String) -> T
    ): NetworkResult<T> = withContext(Dispatchers.IO) {
        val request = buildOcsRequest("$baseUrl$endpoint", method, body)
        try {
            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body.string()

            if (!response.isSuccessful) {
                debugLogger.logResponse(endpoint, method, response.code, responseBody, isError = true)
                return@withContext NetworkResult.ServerError(
                    OCSSerializer.json.decodeFromString<OcsResponse<String>>(responseBody)
                )
            }

            if (responseBody.isBlank()) {
                debugLogger.logResponse(endpoint, method, response.code, responseBody, isError = false)
                return@withContext NetworkResult.Success(parse(responseBody))
            }

            val envelope = OCSSerializer.json.decodeFromString<OcsResponse<JsonElement>>(responseBody)
            if (envelope.ocs.meta.status != OCS_OK) {
                debugLogger.logResponse(endpoint, method, response.code, responseBody, isError = true)
                NetworkResult.ServerError(OcsResponse(Ocs(envelope.ocs.meta, envelope.ocs.meta.message)))
            } else {
                debugLogger.logResponse(endpoint, method, response.code, responseBody, isError = false)
                NetworkResult.Success(parse(responseBody))
            }
        } catch (e: Exception) {
            debugLogger.logException(endpoint, method, e)
            NetworkResult.fromException(e)
        }
    }
}
