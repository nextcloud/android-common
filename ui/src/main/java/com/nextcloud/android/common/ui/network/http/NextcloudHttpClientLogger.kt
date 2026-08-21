/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.network.http

import android.util.Log
import kotlinx.serialization.json.Json
import okhttp3.RequestBody
import okio.Buffer

internal class NextcloudHttpClientLogger(private val enabled: Boolean) {

    companion object {
        private const val TAG = "NextcloudHttpClient"

        private val prettyJson = Json {
            prettyPrint = true
            prettyPrintIndent = "  "
            ignoreUnknownKeys = true
        }
    }

    fun logRequest(endpoint: String, method: HttpMethod, body: RequestBody?) {
        if (!enabled) return

        Log.d(
            TAG,
            """
            |➡️ REQUEST [$method] $endpoint
            |Body:
            |${body.readableString().prettyPrintJsonOrRaw()}
            """.trimMargin()
        )
    }

    fun logResponse(
        endpoint: String,
        method: HttpMethod,
        code: Int,
        responseBody: String,
        isError: Boolean
    ) {
        if (!enabled) return

        Log.d(
            TAG,
            """
            |${statusEmojiFor(code, isError)} RESPONSE [$method] $endpoint  (HTTP $code)
            |Body:
            |${responseBody.ifBlank { "<empty>" }.prettyPrintJsonOrRaw()}
            """.trimMargin()
        )
    }

    fun logException(endpoint: String, method: HttpMethod, e: Exception) {
        if (!enabled) return

        Log.e(TAG, "💥 EXCEPTION [$method] $endpoint -> ${e.javaClass.simpleName}: ${e.message}", e)
    }

    private fun statusEmojiFor(code: Int, isError: Boolean): String = when {
        isError -> "🔴"
        code in 200..299 -> "🟢"
        code in 300..399 -> "🟡"
        else -> "🔴"
    }

    private fun RequestBody?.readableString(): String {
        if (this == null) return "<none>"
        return try {
            val buffer = Buffer()
            writeTo(buffer)
            buffer.readUtf8()
        } catch (e: Exception) {
            "<unable to read body: ${e.message}>"
        }
    }

    private fun String.prettyPrintJsonOrRaw(): String {
        if (this.isBlank() || this == "<none>" || this == "<empty>") return this
        return try {
            val element = Json.parseToJsonElement(this)
            prettyJson.encodeToString(kotlinx.serialization.json.JsonElement.serializer(), element)
        } catch (_: Exception) {
            this
        }
    }
}
