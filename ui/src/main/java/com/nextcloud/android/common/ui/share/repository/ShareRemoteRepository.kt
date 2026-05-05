/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.repository

import com.nextcloud.android.common.ui.network.api.ApiHttpClient
import com.nextcloud.android.common.ui.network.api.ApiMethod
import com.nextcloud.android.common.ui.network.model.ApiResult
import com.nextcloud.android.common.ui.network.model.Meta
import com.nextcloud.android.common.ui.network.model.Ocs
import com.nextcloud.android.common.ui.network.model.OcsResponse
import com.nextcloud.android.common.ui.share.model.api.create.CreateShareRequest
import com.nextcloud.android.common.ui.share.model.api.create.ShareDataResponse
import com.nextcloud.android.common.ui.share.model.api.create.toUnifiedShare
import com.nextcloud.android.common.ui.share.model.api.recipients.ShareRecipients
import com.nextcloud.android.common.ui.share.model.api.update.UpdateShareRequest
import com.nextcloud.android.common.ui.share.model.ui.UnifiedShare
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class ShareRemoteRepository(private val client: ApiHttpClient) : ShareRepository {

    companion object {
        private const val SHARE_ENDPOINT = "/ocs/v2.php/apps/sharing/api/v1/share/"
        private const val SHARES_ENDPOINT = "/ocs/v2.php/apps/sharing/api/v1/shares"
    }

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()
    private val baseUrl get() = client.credentials.baseURL.trimEnd('/')

    private fun errorResult(e: Exception): ApiResult.Error =
        ApiResult.Error(
            OcsResponse(
                Ocs(
                    meta = Meta(
                        status = "error",
                        statusCode = -1,
                        message = e.message ?: "Unknown error"
                    ),
                    data = e.message ?: "Unknown error"
                )
            )
        )

    /**
     *  Searches for recipients
     */
    override suspend fun fetchRecipients(
        recipientType: String,
        query: String,
        limit: Int,
        offset: Int
    ): ApiResult<List<ShareRecipients>> = withContext(Dispatchers.IO) {
        val url = "$baseUrl/ocs/v2.php/apps/sharing/api/v1/recipients" +
            "?recipientType=$recipientType&query=$query&limit=$limit&offset=$offset"

        val request = client.buildRequest(url, ApiMethod.GET)

        try {
            val response = client.okHttpClient.newCall(request).execute()
            val body = response.body.string()
            if (response.isSuccessful) {
                val parsed = json.decodeFromString<OcsResponse<List<ShareRecipients>>>(body)
                ApiResult.Success(parsed.ocs.data)
            } else {
                ApiResult.Error(json.decodeFromString<OcsResponse<String>>(body))
            }
        } catch (e: Exception) {
            errorResult(e)
        }
    }

    override suspend fun createShare(request: CreateShareRequest): ApiResult<ShareDataResponse> =
        withContext(Dispatchers.IO) {
            val url = "$baseUrl$SHARE_ENDPOINT"
            val body = json.encodeToString(request).toRequestBody(jsonMediaType)

            val httpRequest = client.buildRequest(url, ApiMethod.POST, body)

            try {
                val response = client.okHttpClient.newCall(httpRequest).execute()
                val responseBody = response.body.string()
                if (response.isSuccessful) {
                    val parsed = json.decodeFromString<OcsResponse<ShareDataResponse>>(responseBody)
                    ApiResult.Success(parsed.ocs.data)
                } else {
                    ApiResult.Error(json.decodeFromString<OcsResponse<String>>(responseBody))
                }
            } catch (e: Exception) {
                errorResult(e)
            }
        }

    override suspend fun fetchShare(id: String): ApiResult<ShareDataResponse> =
        withContext(Dispatchers.IO) {
            val url = "$baseUrl$SHARE_ENDPOINT$id"
            val request = client.buildRequest(url, ApiMethod.GET)

            try {
                val response = client.okHttpClient.newCall(request).execute()
                val body = response.body.string()
                if (response.isSuccessful) {
                    val parsed = json.decodeFromString<OcsResponse<ShareDataResponse>>(body)
                    ApiResult.Success(parsed.ocs.data)
                } else {
                    ApiResult.Error(json.decodeFromString<OcsResponse<String>>(body))
                }
            } catch (e: Exception) {
                errorResult(e)
            }
        }

    override suspend fun updateShare(id: String, request: UpdateShareRequest): ApiResult<ShareDataResponse> =
        withContext(Dispatchers.IO) {
            val url = "$baseUrl$SHARE_ENDPOINT$id"
            val body = json.encodeToString(request).toRequestBody(jsonMediaType)

            val httpRequest = client.buildRequest(url, ApiMethod.PUT, body)

            try {
                val response = client.okHttpClient.newCall(httpRequest).execute()
                val responseBody = response.body.string()
                if (response.isSuccessful) {
                    val parsed = json.decodeFromString<OcsResponse<ShareDataResponse>>(responseBody)
                    ApiResult.Success(parsed.ocs.data)
                } else {
                    ApiResult.Error(json.decodeFromString<OcsResponse<String>>(responseBody))
                }
            } catch (e: Exception) {
                errorResult(e)
            }
        }

    override suspend fun deleteShare(id: String): ApiResult<Unit> =
        withContext(Dispatchers.IO) {
            val url = "$baseUrl$SHARE_ENDPOINT$id"
            val request = client.buildRequest(url, ApiMethod.DELETE)

            try {
                val response = client.okHttpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    ApiResult.Success(Unit)
                } else {
                    val body = response.body.string()
                    ApiResult.Error(json.decodeFromString<OcsResponse<String>>(body))
                }
            } catch (e: Exception) {
                errorResult(e)
            }
        }

    /**
     * @param sourceType
     * Optional filter to return only shares matching a specific source type.
     * When null, shares of all source types are returned.
     *
     * @param lastShareId
     * Pagination cursor representing the last known share ID.
     * Only shares with an ID greater than this value will be returned.
     * When null, results start from the first available share.
     */
    override suspend fun fetchShares(
        sourceType: String?,
        lastShareId: String?,
        limit: Int
    ): ApiResult<List<UnifiedShare>> = withContext(Dispatchers.IO) {
        val queryParams = buildString {
            append("?limit=$limit")
            sourceType?.let { append("&sourceType=$it") }
            lastShareId?.let { append("&lastShareId=$it") }
        }
        val url = "$baseUrl$SHARES_ENDPOINT$queryParams"
        val request = client.buildRequest(url, ApiMethod.GET)

        try {
            val response = client.okHttpClient.newCall(request).execute()
            val body = response.body.string()
            if (response.isSuccessful) {
                val parsed = json.decodeFromString<OcsResponse<List<ShareDataResponse>>>(body)
                ApiResult.Success(parsed.ocs.data.map { it.toUnifiedShare() })
            } else {
                ApiResult.Error(json.decodeFromString<OcsResponse<String>>(body))
            }
        } catch (e: Exception) {
            errorResult(e)
        }
    }
}
