/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.repository

import com.nextcloud.android.common.ui.network.http.HttpMethod
import com.nextcloud.android.common.ui.network.http.JSON_CONTENT_TYPE
import com.nextcloud.android.common.ui.network.http.NextcloudHttpClient
import com.nextcloud.android.common.ui.network.model.NetworkResult
import com.nextcloud.android.common.ui.network.model.OcsResponse
import com.nextcloud.android.common.ui.network.serialization.OCSSerializer
import com.nextcloud.android.common.ui.share.model.api.create.CreateShareRequest
import com.nextcloud.android.common.ui.share.model.api.create.ShareDataResponse
import com.nextcloud.android.common.ui.share.model.api.create.toUnifiedShare
import com.nextcloud.android.common.ui.share.model.api.recipients.ShareRecipients
import com.nextcloud.android.common.ui.share.model.api.update.UpdateShareRequest
import com.nextcloud.android.common.ui.share.model.ui.UnifiedShare
import okhttp3.RequestBody.Companion.toRequestBody

class ShareRemoteRepository(
    private val client: NextcloudHttpClient,
    private val json: kotlinx.serialization.json.Json = OCSSerializer.json
) : ShareRepository {

    private companion object {
        // Trailing slash intentional, share ID is appended directly: "$SHARE_ENDPOINT$id"
        private const val SHARE_ENDPOINT = "/ocs/v2.php/apps/sharing/api/v1/share/"

        private const val SHARES_ENDPOINT = "/ocs/v2.php/apps/sharing/api/v1/shares"
        private const val RECIPIENTS_ENDPOINT = "/ocs/v2.php/apps/sharing/api/v1/recipients"
    }

    override suspend fun fetchRecipients(
        recipientType: String,
        query: String,
        limit: Int,
        offset: Int
    ): NetworkResult<List<ShareRecipients>> =
        client.executeRequest(
            endpoint = "$RECIPIENTS_ENDPOINT?recipientType=$recipientType&query=$query&limit=$limit&offset=$offset",
            method = HttpMethod.GET
        ) { body ->
            json.decodeFromString<OcsResponse<List<ShareRecipients>>>(body).ocs.data
        }

    override suspend fun createShare(request: CreateShareRequest): NetworkResult<ShareDataResponse> =
        client.executeRequest(
            endpoint = SHARE_ENDPOINT,
            method = HttpMethod.POST,
            body = json.encodeToString(request).toRequestBody(JSON_CONTENT_TYPE)
        ) { body ->
            json.decodeFromString<OcsResponse<ShareDataResponse>>(body).ocs.data
        }

    override suspend fun fetchShare(id: String): NetworkResult<ShareDataResponse> =
        client.executeRequest(
            endpoint = "$SHARE_ENDPOINT$id",
            method = HttpMethod.GET
        ) { body ->
            json.decodeFromString<OcsResponse<ShareDataResponse>>(body).ocs.data
        }

    override suspend fun updateShare(id: String, request: UpdateShareRequest): NetworkResult<ShareDataResponse> =
        client.executeRequest(
            endpoint = "$SHARE_ENDPOINT$id",
            method = HttpMethod.PUT,
            body = json.encodeToString(request).toRequestBody(JSON_CONTENT_TYPE)
        ) { body ->
            json.decodeFromString<OcsResponse<ShareDataResponse>>(body).ocs.data
        }

    override suspend fun deleteShare(id: String): NetworkResult<Unit> =
        client.executeRequest(
            endpoint = "$SHARE_ENDPOINT$id",
            method = HttpMethod.DELETE
        ) { }

    override suspend fun fetchShares(
        sourceType: String?,
        lastShareId: String?,
        limit: Int
    ): NetworkResult<List<UnifiedShare>> {
        val queryParams = buildString {
            append("?limit=$limit")
            sourceType?.let { append("&sourceType=$it") }
            lastShareId?.let { append("&lastShareId=$it") }
        }
        return client.executeRequest(
            endpoint = "$SHARES_ENDPOINT$queryParams",
            method = HttpMethod.GET
        ) { body ->
            json.decodeFromString<OcsResponse<List<ShareDataResponse>>>(body)
                .ocs.data
                .map { it.toUnifiedShare() }
        }
    }
}
