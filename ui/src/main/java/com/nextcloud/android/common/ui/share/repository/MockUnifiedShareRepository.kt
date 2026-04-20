/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.repository

import com.nextcloud.android.common.ui.share.model.UnifiedShareCategory
import com.nextcloud.android.common.ui.share.model.UnifiedShareDownloadLimit
import com.nextcloud.android.common.ui.share.model.UnifiedSharePermission
import com.nextcloud.android.common.ui.share.model.UnifiedShareType
import com.nextcloud.android.common.ui.share.model.UnifiedShares

class MockUnifiedShareRepository: UnifiedShareRepository {
    override suspend fun fetchShares(): List<UnifiedShares> {
        return listOf(
            UnifiedShares(
                id = 1,
                password = "",
                note = "Design review – please check latest changes",
                limit = UnifiedShareDownloadLimit(
                    limit = 100,
                    downloadCount = 12
                ),
                expirationDate = 0,
                permission = UnifiedSharePermission.CanView,
                label = "Alice Johnson",
                sharedTo = "alice@company.com",
                type = UnifiedShareType.InternalUser,
                category = UnifiedShareCategory.Invited
            ),

            UnifiedShares(
                id = 2,
                password = "",
                note = "",
                limit = UnifiedShareDownloadLimit(
                    limit = 0,
                    downloadCount = 0
                ),
                expirationDate = 0,
                permission = UnifiedSharePermission.CanEdit,
                label = "Marketing Team",
                sharedTo = "marketing",
                type = UnifiedShareType.InternalGroup,
                category = UnifiedShareCategory.Invited
            ),

            UnifiedShares(
                id = 3,
                password = "1234",
                note = "Public link for client review",
                limit = UnifiedShareDownloadLimit(
                    limit = 50,
                    downloadCount = 5
                ),
                expirationDate = 1710000000,
                permission = UnifiedSharePermission.Custom(
                    read = true,
                    edit = false,
                    delete = false,
                    create = false
                ),
                label = "Public Link",
                sharedTo = "https://nextcloud.com/s/abc123",
                type = UnifiedShareType.InternalLink,
                category = UnifiedShareCategory.Anyone
            ),

            UnifiedShares(
                id = 4,
                password = "",
                note = "External partner access",
                limit = UnifiedShareDownloadLimit(
                    limit = 20,
                    downloadCount = 2
                ),
                expirationDate = 0,
                permission = UnifiedSharePermission.CanView,
                label = "John External",
                sharedTo = "john@external.com",
                type = UnifiedShareType.ExternalMail,
                category = UnifiedShareCategory.Anyone
            ),

            UnifiedShares(
                id = 5,
                password = "",
                note = "Federated sharing with partner instance",
                limit = UnifiedShareDownloadLimit(
                    limit = 0,
                    downloadCount = 0
                ),
                expirationDate = 0,
                permission = UnifiedSharePermission.FileDrop,
                label = "Partner Cloud",
                sharedTo = "partner@nextcloud.org",
                type = UnifiedShareType.ExternalFederated,
                category = UnifiedShareCategory.Anyone
            )
        )
    }
}
