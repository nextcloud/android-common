/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.network.http

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody

val JSON_CONTENT_TYPE = "application/json; charset=utf-8".toMediaType()

private const val HEADER_ACCEPT = "Accept"
private const val HEADER_ACCEPT_VALUE = "application/json"

fun buildOcsRequest(
    url: String,
    method: HttpMethod,
    body: RequestBody? = null
): Request = Request.Builder()
    .url(url)
    .header(HEADER_ACCEPT, HEADER_ACCEPT_VALUE)
    .method(method.type, body)
    .build()
