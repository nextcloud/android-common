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
import com.nextcloud.android.common.ui.share.model.api.recipients.Recipient
import com.nextcloud.android.common.ui.share.model.api.request.AddRecipientRequest
import com.nextcloud.android.common.ui.share.model.api.request.AddSourceRequest
import com.nextcloud.android.common.ui.share.model.api.request.GetShareRequest
import com.nextcloud.android.common.ui.share.model.api.request.UpdateSharePermissionRequest
import com.nextcloud.android.common.ui.share.model.api.request.UpdateSharePropertyRequest
import com.nextcloud.android.common.ui.share.model.api.request.UpdateShareRecipientSecretRequest
import com.nextcloud.android.common.ui.share.model.api.request.UpdateShareStateRequest
import com.nextcloud.android.common.ui.share.model.api.share.Share
import okhttp3.RequestBody.Companion.toRequestBody

class ShareRemoteRepository(
    private val client: NextcloudHttpClient,
    private val json: kotlinx.serialization.json.Json = OCSSerializer.json
) : ShareRepository {

    private companion object {
        private const val SHARE_ENDPOINT = "/ocs/v2.php/apps/sharing/api/v1/share"
        private const val SHARES_ENDPOINT = "/ocs/v2.php/apps/sharing/api/v1/shares"
        private const val RECIPIENTS_ENDPOINT = "/ocs/v2.php/apps/sharing/api/v1/recipients"
    }

    override suspend fun fetchRecipients(
        recipientTypeClasses: List<String>?,
        query: String,
        limit: Int,
        offset: Int
    ): NetworkResult<List<Recipient>> {
        val queryParams = buildString {
            append("?query=$query&limit=$limit&offset=$offset")
            recipientTypeClasses?.forEach { append("&recipientTypeClasses[]=$it") }
        }
        return client.executeRequest(
            endpoint = "$RECIPIENTS_ENDPOINT$queryParams",
            method = HttpMethod.GET
        ) { body ->
            json.decodeFromString<OcsResponse<List<Recipient>>>(body).ocs.data
        }
    }

    override suspend fun createDraftShare(): NetworkResult<Share> =
        client.executeRequest(
            endpoint = SHARE_ENDPOINT,
            method = HttpMethod.POST,
            body = ByteArray(0).toRequestBody()
        ) { body ->
            json.decodeFromString<OcsResponse<Share>>(body).ocs.data
        }

    override suspend fun fetchShare(
        id: String,
        request: GetShareRequest
    ): NetworkResult<Share> =
        client.executeRequest(
            endpoint = "$SHARE_ENDPOINT/$id",
            method = HttpMethod.POST,
            body = json.encodeToString(request).toRequestBody(JSON_CONTENT_TYPE)
        ) { body ->
            json.decodeFromString<OcsResponse<Share>>(body).ocs.data
        }

    override suspend fun deleteShare(id: String): NetworkResult<Unit> =
        client.executeRequest(
            endpoint = "$SHARE_ENDPOINT/$id",
            method = HttpMethod.DELETE
        ) { }

    override suspend fun fetchShares(
        sourceClass: String?,
        lastShareID: String?,
        limit: Int
    ): NetworkResult<List<Share>> {
        val queryParams = buildString {
            append("?limit=$limit")
            sourceClass?.let { append("&sourceClass=$it") }
            lastShareID?.let { append("&lastShareID=$it") }
        }
        return client.executeRequest(
            endpoint = "$SHARES_ENDPOINT$queryParams",
            method = HttpMethod.GET
        ) { body ->
            json.decodeFromString<OcsResponse<List<Share>>>(body).ocs.data
        }
    }

    override suspend fun updateShareState(
        id: String,
        request: UpdateShareStateRequest
    ): NetworkResult<Share> =
        client.executeRequest(
            endpoint = "$SHARE_ENDPOINT/$id/state",
            method = HttpMethod.PUT,
            body = json.encodeToString(request).toRequestBody(JSON_CONTENT_TYPE)
        ) { body ->
            json.decodeFromString<OcsResponse<Share>>(body).ocs.data
        }

    override suspend fun addShareSource(
        id: String,
        request: AddSourceRequest
    ): NetworkResult<Share> =
        client.executeRequest(
            endpoint = "$SHARE_ENDPOINT/$id/source",
            method = HttpMethod.POST,
            body = json.encodeToString(request).toRequestBody(JSON_CONTENT_TYPE)
        ) { body ->
            json.decodeFromString<OcsResponse<Share>>(body).ocs.data
        }

    override suspend fun removeShareSource(
        id: String,
        clazz: String,
        value: String
    ): NetworkResult<Share> =
        client.executeRequest(
            endpoint = "$SHARE_ENDPOINT/$id/source?class=$clazz&value=$value",
            method = HttpMethod.DELETE
        ) { body ->
            json.decodeFromString<OcsResponse<Share>>(body).ocs.data
        }

    override suspend fun addShareRecipient(
        id: String,
        request: AddRecipientRequest
    ): NetworkResult<Share> =
        client.executeRequest(
            endpoint = "$SHARE_ENDPOINT/$id/recipient",
            method = HttpMethod.POST,
            body = json.encodeToString(request).toRequestBody(JSON_CONTENT_TYPE)
        ) { body ->
            json.decodeFromString<OcsResponse<Share>>(body).ocs.data
        }

    override suspend fun removeShareRecipient(
        id: String,
        clazz: String,
        value: String,
        instance: String?
    ): NetworkResult<Share> {
        val queryParams = buildString {
            append("?class=$clazz&value=$value")
            instance?.let { append("&instance=$it") }
        }
        return client.executeRequest(
            endpoint = "$SHARE_ENDPOINT/$id/recipient$queryParams",
            method = HttpMethod.DELETE
        ) { body ->
            json.decodeFromString<OcsResponse<Share>>(body).ocs.data
        }
    }

    override suspend fun updateShareProperty(
        id: String,
        request: UpdateSharePropertyRequest
    ): NetworkResult<Share> =
        client.executeRequest(
            endpoint = "$SHARE_ENDPOINT/$id/property",
            method = HttpMethod.PUT,
            body = json.encodeToString(request).toRequestBody(JSON_CONTENT_TYPE)
        ) { body ->
            json.decodeFromString<OcsResponse<Share>>(body).ocs.data
        }

    override suspend fun updateSharePermission(
        id: String,
        request: UpdateSharePermissionRequest
    ): NetworkResult<Share> =
        client.executeRequest(
            endpoint = "$SHARE_ENDPOINT/$id/enabled",
            method = HttpMethod.PUT,
            body = json.encodeToString(request).toRequestBody(JSON_CONTENT_TYPE)
        ) { body ->
            json.decodeFromString<OcsResponse<Share>>(body).ocs.data
        }

    override suspend fun updateShareRecipientSecret(
        id: String,
        request: UpdateShareRecipientSecretRequest
    ): NetworkResult<Share> =
        client.executeRequest(
            endpoint = "$SHARE_ENDPOINT/$id/recipient/secret",
            method = HttpMethod.PUT,
            body = json.encodeToString(request).toRequestBody(JSON_CONTENT_TYPE)
        ) { body ->
            json.decodeFromString<OcsResponse<Share>>(body).ocs.data
        }
}
