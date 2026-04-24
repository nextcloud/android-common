/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.repository

import com.nextcloud.android.common.ui.network.model.ApiResult
import com.nextcloud.android.common.ui.share.model.api.create.CreateShareRequest
import com.nextcloud.android.common.ui.share.model.api.create.ShareDataResponse
import com.nextcloud.android.common.ui.share.model.api.recipients.ShareRecipients
import com.nextcloud.android.common.ui.share.model.api.update.UpdateShareRequest
import com.nextcloud.android.common.ui.share.model.ui.UnifiedShare

class ShareRemoteRepository: ShareRepository {

    // TODO: ALL OCS-APIRequest //boolean header

    /**
     *  Searches for recipients
     */
    override suspend fun fetchRecipients(
        recipientType: String,
        query: String,
        limit: Int,
        offset: Int
    ): ApiResult<List<ShareRecipients>> {
        /*
            GET
            /ocs/v2.php/apps/sharing/api/v1/recipients

         */

        TODO("Not yet implemented")
    }

    override suspend fun createShare(request: CreateShareRequest): ApiResult<ShareDataResponse> {
        /*
             POST
             /ocs/v2.php/apps/sharing/api/v1/share
         */
        TODO("Not yet implemented")
    }

    override suspend fun fetchShare(id: String): ApiResult<ShareDataResponse> {
        /*
             POST
             /ocs/v2.php/apps/sharing/api/v1/share/{id}
         */
        TODO("Not yet implemented")
    }

    override suspend fun updateShare(id: String, request: UpdateShareRequest): ApiResult<ShareDataResponse> {
        /*
            PUT
            /ocs/v2.php/apps/sharing/api/v1/share/{id}
         */
        TODO("Not yet implemented")
    }

    override suspend fun deleteShare(id: String): ApiResult<Unit> {
        /*
            DELETE
            /ocs/v2.php/apps/sharing/api/v1/share/{id}
         */
        TODO("Not yet implemented")
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
    ): ApiResult<List<UnifiedShare>> {
        /*
            GET
            /ocs/v2.php/apps/sharing/api/v1/shares
         */
        TODO("Not yet implemented")
    }
}
