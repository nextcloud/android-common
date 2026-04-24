/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.network.api

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class ApiHttpClient private constructor(
    val okHttpClient: OkHttpClient,
    val credentials: ApiCredentials
) {
    companion object {
        private const val CONNECT_TIMEOUT_SECONDS = 30L
        private const val READ_TIMEOUT_SECONDS = 30L
        private const val WRITE_TIMEOUT_SECONDS = 30L

        fun create(
            credentials: ApiCredentials,
            enableLogging: Boolean = false
        ): ApiHttpClient {
            val authInterceptor = AuthInterceptor(credentials)

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
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build()

            return ApiHttpClient(okHttpClient, credentials)
        }
    }

    private class AuthInterceptor(private val credentials: ApiCredentials) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val basicCredentials = Credentials.basic(credentials.username, credentials.token)

            val request = chain.request()
                .newBuilder()
                .header("Authorization", basicCredentials)
                .header("OCS-APIRequest", "true")
                .url(buildUrl(chain.request().url.toString(), credentials.baseURL))
                .build()

            return chain.proceed(request)
        }

        private fun buildUrl(requestUrl: String, baseUrl: String): String {
            return if (requestUrl.startsWith("http://") || requestUrl.startsWith("https://")) {
                requestUrl
            } else {
                "${baseUrl.trimEnd('/')}/${requestUrl.trimStart('/')}"
            }
        }
    }
}
