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
import com.nextcloud.android.common.ui.share.model.api.owner.Owner
import com.nextcloud.android.common.ui.share.model.api.recipients.ShareRecipients
import com.nextcloud.android.common.ui.share.model.api.update.UpdateShareRequest
import com.nextcloud.android.common.ui.share.model.api.user.ShareUser
import com.nextcloud.android.common.ui.share.model.ui.*

class MockShareRepository : ShareRepository {
    override suspend fun fetchRecipients(
        recipientType: String,
        query: String,
        limit: Int,
        offset: Int
    ): ApiResult<List<ShareRecipients>> {

        val mock = listOf(
            ShareRecipients(
                type = recipientType,
                value = "alice@company.com",
                displayName = "Alice Johnson",
                displayNameUnique = "Alice Johnson (Company)",
                iconUrlLight = "https://mock/icons/user_light.png",
                iconUrlDark = "https://mock/icons/user_dark.png"
            ),

            ShareRecipients(
                type = recipientType,
                value = "marketing",
                displayName = "Marketing Team",
                displayNameUnique = "Marketing Team (Group)",
                iconUrlLight = "https://mock/icons/group_light.png",
                iconUrlDark = "https://mock/icons/group_dark.png"
            ),

            ShareRecipients(
                type = recipientType,
                value = "john@external.com",
                displayName = "John External",
                displayNameUnique = "John External (External)",
                iconUrlLight = "https://mock/icons/external_light.png",
                iconUrlDark = "https://mock/icons/external_dark.png"
            )
        )

        return ApiResult.Success(mock)
    }

    override suspend fun createShare(
        request: CreateShareRequest
    ): ApiResult<ShareDataResponse> {

        val response = ShareDataResponse(
            sources = request.data.sources,
            recipients = request.data.recipients,
            properties = request.data.properties,
            id = "mock-share-${System.currentTimeMillis()}",
            lastUpdated = System.currentTimeMillis(),
            owner = Owner(
                userId = "mock-user",
                displayName = "Mock User"
            )
        )

        return ApiResult.Success(response)
    }

    override suspend fun fetchShare(id: String): ApiResult<ShareDataResponse> {

        val mock = ShareDataResponse(
            sources = emptyList(),
            recipients = listOf(
                ShareUser(
                    type = "user",
                    value = "alice@company.com",
                    displayName = "Alice Johnson"
                )
            ),
            properties = emptyMap(),
            id = id,
            lastUpdated = 0,
            owner = Owner(
                userId = "alice",
                displayName = "Alice Johnson"
            )
        )

        return ApiResult.Success(mock)
    }

    override suspend fun updateShare(
        id: String,
        request: UpdateShareRequest
    ): ApiResult<ShareDataResponse> {

        val updated = ShareDataResponse(
            sources = request.data.sources,
            recipients = request.data.recipients,
            properties = request.data.properties,
            id = id,
            lastUpdated = System.currentTimeMillis(),
            owner = request.data.owner
        )

        return ApiResult.Success(updated)
    }

    override suspend fun deleteShare(id: String): ApiResult<Unit> {
        return ApiResult.Success(Unit)
    }

    override suspend fun fetchShares(
        sourceType: String?,
        lastShareId: String?,
        limit: Int
    ): ApiResult<List<UnifiedShare>> {
        val data = listOf(
            UnifiedShare(
                id = "1",
                sources = emptyList(),
                recipients = listOf(
                    ShareUser(
                        type = "user",
                        value = "alice@company.com",
                        displayName = "Alice Johnson"
                    )
                ),
                properties = emptyMap(),
                lastUpdated = 0,
                owner = Owner(
                    userId = "alice",
                    displayName = "Alice Johnson"
                ),

                permission = UnifiedSharePermission.CanView,
                label = "Alice Johnson",
                note = "Design review – please check latest changes",
                password = "",
                type = UnifiedShareType.InternalUser,
                category = UnifiedShareCategory.Invited,
                limit = UnifiedShareDownloadLimit(
                    limit = 100,
                    downloadCount = 12
                )
            ),

            UnifiedShare(
                id = "2",
                sources = emptyList(),
                recipients = listOf(
                    ShareUser(
                        type = "group",
                        value = "marketing",
                        displayName = "Marketing Team"
                    )
                ),
                properties = emptyMap(),
                lastUpdated = 0,
                owner = Owner(
                    userId = "system",
                    displayName = "System"
                ),

                permission = UnifiedSharePermission.CanEdit,
                label = "Marketing Team",
                note = "",
                password = "",
                type = UnifiedShareType.InternalGroup,
                category = UnifiedShareCategory.Invited,
                limit = UnifiedShareDownloadLimit(
                    limit = 0,
                    downloadCount = 0
                )
            ),

            UnifiedShare(
                id = "3",
                sources = listOf(
                    ShareUser(
                        type = "link",
                        value = "https://nextcloud.com/s/abc123",
                        displayName = "Public Link"
                    )
                ),
                recipients = emptyList(),
                properties = emptyMap(),
                lastUpdated = 1710000000,
                owner = Owner(
                    userId = "system",
                    displayName = "System"
                ),

                permission = UnifiedSharePermission.Custom(
                    read = true,
                    edit = false,
                    delete = false,
                    create = false
                ),
                label = "Public Link",
                note = "Public link for client review",
                password = "1234",
                type = UnifiedShareType.InternalLink,
                category = UnifiedShareCategory.Anyone,
                limit = UnifiedShareDownloadLimit(
                    limit = 50,
                    downloadCount = 5
                )
            ),

            UnifiedShare(
                id = "4",
                sources = emptyList(),
                recipients = listOf(
                    ShareUser(
                        type = "mail",
                        value = "john@external.com",
                        displayName = "John External"
                    )
                ),
                properties = emptyMap(),
                lastUpdated = 0,
                owner = Owner(
                    userId = "john",
                    displayName = "John External"
                ),

                permission = UnifiedSharePermission.CanView,
                label = "John External",
                note = "External partner access",
                password = "",
                type = UnifiedShareType.ExternalMail,
                category = UnifiedShareCategory.Anyone,
                limit = UnifiedShareDownloadLimit(
                    limit = 20,
                    downloadCount = 2
                )
            ),

            UnifiedShare(
                id = "5",
                sources = emptyList(),
                recipients = listOf(
                    ShareUser(
                        type = "federated",
                        value = "partner@nextcloud.org",
                        displayName = "Partner Cloud"
                    )
                ),
                properties = emptyMap(),
                lastUpdated = 0,
                owner = Owner(
                    userId = "partner",
                    displayName = "Partner Cloud"
                ),

                permission = UnifiedSharePermission.FileDrop,
                label = "Partner Cloud",
                note = "Federated sharing with partner instance",
                password = "",
                type = UnifiedShareType.ExternalFederated,
                category = UnifiedShareCategory.Anyone,
                limit = UnifiedShareDownloadLimit(
                    limit = 0,
                    downloadCount = 0
                )
            )
        )

        return ApiResult.Success(data)
    }
}
