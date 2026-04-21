/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.repository

import com.nextcloud.android.common.ui.network.ApiResult
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
    ): ApiResult<List<ShareRecipients>>

    suspend fun createShare(request: CreateShareRequest): ApiResult<ShareDataResponse>

    suspend fun fetchShare(id: String): ApiResult<ShareDataResponse>

    suspend fun updateShare(id: String, request: UpdateShareRequest): ApiResult<ShareDataResponse>

    suspend fun deleteShare(id: String): ApiResult<Unit>

    suspend fun fetchShares(
        sourceType: String? = null,
        lastShareId: String? = null,
        limit: Int = 100
    ): ApiResult<List<UnifiedShare>>
}
