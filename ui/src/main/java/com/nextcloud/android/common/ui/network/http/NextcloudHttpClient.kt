/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.network.http

import com.nextcloud.android.common.ui.network.auth.AuthInterceptor
import com.nextcloud.android.common.ui.network.auth.ServerCredentials
import com.nextcloud.android.common.ui.network.model.NetworkResult
import com.nextcloud.android.common.ui.network.model.OcsResponse
import com.nextcloud.android.common.ui.network.serialization.OCSSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class NextcloudHttpClient private constructor(
    val okHttpClient: OkHttpClient,
    val credentials: ServerCredentials
) {
    companion object {
        private const val CONNECT_TIMEOUT_SECONDS = 90L
        private const val READ_TIMEOUT_SECONDS = 90L
        private const val WRITE_TIMEOUT_SECONDS = 90L

        fun create(
            credentials: ServerCredentials,
            enableLogging: Boolean = false
        ): NextcloudHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = if (enableLogging) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(AuthInterceptor(credentials))
                .addInterceptor(loggingInterceptor)
                .build()

            return NextcloudHttpClient(okHttpClient, credentials)
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
            if (response.isSuccessful) {
                NetworkResult.Success(parse(responseBody))
            } else {
                NetworkResult.ServerError(OCSSerializer.json.decodeFromString<OcsResponse<String>>(responseBody))
            }
        } catch (e: Exception) {
            NetworkResult.fromException(e)
        }
    }
}
