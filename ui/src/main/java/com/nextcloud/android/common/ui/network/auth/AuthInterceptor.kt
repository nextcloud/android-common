/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.network.auth

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val credentials: ServerCredentials) : Interceptor {

    private companion object {
        private const val HTTP_PREFIX = "http://"
        private const val HTTPS_PREFIX = "https://"
        private const val DELIMITER = '/'

        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val HEADER_OCS_REQUEST = "OCS-APIRequest"
        private const val HEADER_OCS_REQUEST_VALUE = "true"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val basicCredentials = Credentials.basic(credentials.username, credentials.token)

        val request = chain.request()
            .newBuilder()
            .header(HEADER_AUTHORIZATION, basicCredentials)
            .header(HEADER_OCS_REQUEST, HEADER_OCS_REQUEST_VALUE)
            .url(resolveUrl(chain.request().url.toString()))
            .build()

        return chain.proceed(request)
    }

    /**
     * Prepends [ServerCredentials.baseURL] to relative URLs.
     * Absolute URLs (starting with http/https) are passed through unchanged.
     */
    private fun resolveUrl(requestUrl: String): String =
        if (requestUrl.startsWith(HTTP_PREFIX) || requestUrl.startsWith(HTTPS_PREFIX)) {
            requestUrl
        } else {
            "${credentials.baseURL.trimEnd(DELIMITER)}/${requestUrl.trimStart(DELIMITER)}"
        }
}
