/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.repository

import com.nextcloud.android.common.ui.network.model.NetworkResult
import com.nextcloud.android.common.ui.share.model.api.create.CreateShareRequest
import com.nextcloud.android.common.ui.share.model.api.create.ShareDataResponse
import com.nextcloud.android.common.ui.share.model.api.recipients.ShareRecipients
import com.nextcloud.android.common.ui.share.model.api.update.UpdateShareRequest
import com.nextcloud.android.common.ui.share.model.ui.UnifiedShare

interface ShareRepository {
    suspend fun fetchRecipients(
        recipientType: String,
        query: String,
        limit: Int = 10,
        offset: Int = 0
    ): NetworkResult<List<ShareRecipients>>

    suspend fun createShare(request: CreateShareRequest): NetworkResult<ShareDataResponse>

    suspend fun fetchShare(id: String): NetworkResult<ShareDataResponse>

    suspend fun updateShare(id: String, request: UpdateShareRequest): NetworkResult<ShareDataResponse>

    suspend fun deleteShare(id: String): NetworkResult<Unit>

    suspend fun fetchShares(
        sourceType: String? = null,
        lastShareId: String? = null,
        limit: Int = 100
    ): NetworkResult<List<UnifiedShare>>
}
