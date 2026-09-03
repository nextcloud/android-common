/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.avatar

import android.util.Log
import com.nextcloud.android.common.ui.network.auth.ServerCredentials
import com.nextcloud.android.common.ui.network.http.NextcloudHttpClient
import com.nextcloud.android.common.ui.network.model.dataOrElse
import com.nextcloud.android.common.ui.share.model.api.share.Share
import com.nextcloud.android.common.ui.share.model.api.source.Source
import com.nextcloud.android.common.ui.share.repository.ShareRemoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShareAvatarRepository(private val credentials: ServerCredentials) {
    companion object {
        private const val TAG = "ShareAvatarRepository"
        private const val SHARES_PAGE_SIZE = 3
    }

    suspend fun fetchShareAvatars(sourceId: String): List<Share>? = withContext(Dispatchers.IO) {
        val client = NextcloudHttpClient.create(credentials)
        val shareRepository = ShareRemoteRepository(client)
        val result =
            shareRepository.fetchShares(
                filterSourceTypeValue = sourceId,
                limit = SHARES_PAGE_SIZE,
                filterSourceTypeClass = Source.NODE_SOURCE_CLASS,
                lastShareID = null
            )
        return@withContext result.dataOrElse { Log.e(TAG, "failed to fetch avatars") }
    }
}
