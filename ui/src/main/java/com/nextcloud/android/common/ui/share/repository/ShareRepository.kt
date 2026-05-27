/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */
package com.nextcloud.android.common.ui.share.repository

import com.nextcloud.android.common.ui.network.model.NetworkResult
import com.nextcloud.android.common.ui.share.model.api.recipients.Recipient
import com.nextcloud.android.common.ui.share.model.api.request.AddRecipientRequest
import com.nextcloud.android.common.ui.share.model.api.request.AddSourceRequest
import com.nextcloud.android.common.ui.share.model.api.request.GetShareRequest
import com.nextcloud.android.common.ui.share.model.api.request.UpdateSharePermissionRequest
import com.nextcloud.android.common.ui.share.model.api.request.UpdateSharePropertyRequest
import com.nextcloud.android.common.ui.share.model.api.request.UpdateShareStateRequest
import com.nextcloud.android.common.ui.share.model.api.share.Share

interface ShareRepository {

    suspend fun fetchRecipients(
        recipientTypeClass: String?,
        query: String,
        limit: Int,
        offset: Int
    ): NetworkResult<List<Recipient>>

    suspend fun createShare(): NetworkResult<Share>

    suspend fun fetchShare(
        id: String,
        request: GetShareRequest = GetShareRequest()
    ): NetworkResult<Share>

    suspend fun deleteShare(id: String): NetworkResult<Unit>

    suspend fun fetchShares(
        sourceClass: String?,
        lastShareID: String?,
        limit: Int
    ): NetworkResult<List<Share>>

    suspend fun updateShareState(
        id: String,
        request: UpdateShareStateRequest
    ): NetworkResult<Share>

    suspend fun addShareSource(
        id: String,
        request: AddSourceRequest
    ): NetworkResult<Share>

    suspend fun removeShareSource(
        id: String,
        clazz: String,
        value: String
    ): NetworkResult<Share>

    suspend fun addShareRecipient(
        id: String,
        request: AddRecipientRequest
    ): NetworkResult<Share>

    suspend fun removeShareRecipient(
        id: String,
        clazz: String,
        value: String,
        instance: String? = null
    ): NetworkResult<Share>

    suspend fun updateShareProperty(
        id: String,
        request: UpdateSharePropertyRequest
    ): NetworkResult<Share>

    suspend fun updateSharePermission(
        id: String,
        request: UpdateSharePermissionRequest
    ): NetworkResult<Share>
}
