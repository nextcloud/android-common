/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.repository

import com.nextcloud.android.common.ui.network.model.NetworkResult
import com.nextcloud.android.common.ui.share.model.api.icon.Icon
import com.nextcloud.android.common.ui.share.model.api.owner.Owner
import com.nextcloud.android.common.ui.share.model.api.permission.Permission
import com.nextcloud.android.common.ui.share.model.api.property.PropertyBoolean
import com.nextcloud.android.common.ui.share.model.api.property.PropertyDate
import com.nextcloud.android.common.ui.share.model.api.property.PropertyPassword
import com.nextcloud.android.common.ui.share.model.api.property.PropertyString
import com.nextcloud.android.common.ui.share.model.api.recipients.Recipient
import com.nextcloud.android.common.ui.share.model.api.request.AddRecipientRequest
import com.nextcloud.android.common.ui.share.model.api.request.AddSourceRequest
import com.nextcloud.android.common.ui.share.model.api.request.GetShareRequest
import com.nextcloud.android.common.ui.share.model.api.request.UpdateSharePermissionRequest
import com.nextcloud.android.common.ui.share.model.api.request.UpdateSharePropertyRequest
import com.nextcloud.android.common.ui.share.model.api.request.UpdateShareStateRequest
import com.nextcloud.android.common.ui.share.model.api.secret.Secret
import com.nextcloud.android.common.ui.share.model.api.share.Share
import com.nextcloud.android.common.ui.share.model.api.source.Source
import com.nextcloud.android.common.ui.share.model.api.state.ShareState

class MockShareRepository : ShareRepository {

    private val mockOwner = Owner(
        userId = "alice",
        displayName = "Alice Johnson",
        icon = Icon(
            light = "https://mock/icons/user_light.png",
            dark = "https://mock/icons/user_dark.png"
        )
    )

    private val mockPermissions = listOf(
        Permission(
            clazz = "read",
            displayName = "Read",
            category = "access",
            enabled = true
        ),
        Permission(
            clazz = "update",
            displayName = "Update",
            category = "access",
            enabled = false
        ),
        Permission(
            clazz = "delete",
            displayName = "Delete",
            category = "access",
            enabled = false
        )
    )

    private val mockProperties = listOf(
        PropertyString(
            clazz = "note",
            displayName = "Note",
            priority = 10,
            required = false,
            value = "Design review – please check latest changes"
        ),
        PropertyPassword(
            clazz = "password",
            displayName = "Password",
            priority = 20,
            required = false,
            value = null
        ),
        PropertyDate(
            clazz = "expiration_date",
            displayName = "Expiration date",
            priority = 30,
            required = false,
            value = null,
            minDate = "2026-01-01",
            maxDate = "2027-01-01"
        ),
        PropertyBoolean(
            clazz = "hide_download",
            displayName = "Hide download",
            priority = 40,
            required = false,
            value = "false"
        )
    )

    private fun buildShare(
        id: String,
        sources: List<Source>,
        recipients: List<Recipient>,
        shareState: ShareState = ShareState.ACTIVE,
        lastUpdated: Long = System.currentTimeMillis(),
        owner: Owner = mockOwner
    ) = Share(
        id = id,
        owner = owner,
        lastUpdated = lastUpdated,
        shareState = shareState,
        sources = sources,
        recipients = recipients,
        properties = mockProperties,
        permissions = mockPermissions
    )

    private val mockShares = mutableListOf(
        buildShare(
            id = "1",
            sources = listOf(
                Source(clazz = "file", value = "/Photos/vacation.jpg", displayName = "vacation.jpg")
            ),
            recipients = listOf(
                Recipient(
                    clazz = "user",
                    value = "alice@company.com",
                    displayName = "Alice Johnson",
                    icon = Icon(
                        light = "https://mock/icons/user_light.png",
                        dark = "https://mock/icons/user_dark.png"
                    ),
                    secret = Secret(false, value = "", url = "")
                )
            )
        ),
        buildShare(
            id = "2",
            sources = listOf(
                Source(clazz = "file", value = "/Documents/report.pdf", displayName = "report.pdf")
            ),
            recipients = listOf(
                Recipient(
                    clazz = "group",
                    value = "marketing",
                    displayName = "Marketing Team",
                    icon = Icon(
                        light = "https://mock/icons/group_light.png",
                        dark = "https://mock/icons/group_dark.png"
                    ),
                    secret = Secret(false, value = "", url = "")
                )
            ),
            owner = Owner(
                userId = "system",
                displayName = "System",
                icon = Icon(
                    light = "https://mock/icons/system_light.png",
                    dark = "https://mock/icons/system_dark.png"
                )
            )
        ),
        buildShare(
            id = "3",
            sources = listOf(
                Source(clazz = "link", value = "https://nextcloud.com/s/abc123", displayName = "Public Link")
            ),
            recipients = emptyList(),
            lastUpdated = 1710000000L,
            owner = Owner(
                userId = "system",
                displayName = "System",
                icon = Icon(
                    light = "https://mock/icons/system_light.png",
                    dark = "https://mock/icons/system_dark.png"
                )
            )
        ),
        buildShare(
            id = "4",
            sources = listOf(
                Source(clazz = "file", value = "/Projects/brief.docx", displayName = "brief.docx")
            ),
            recipients = listOf(
                Recipient(
                    clazz = "mail",
                    value = "john@external.com",
                    displayName = "John External",
                    icon = Icon(
                        light = "https://mock/icons/external_light.png",
                        dark = "https://mock/icons/external_dark.png"
                    ),
                    secret = Secret(false, value = "", url = "")
                )
            ),
            owner = Owner(
                userId = "john",
                displayName = "John External",
                icon = Icon(
                    light = "https://mock/icons/user_light.png",
                    dark = "https://mock/icons/user_dark.png"
                )
            )
        ),
        buildShare(
            id = "5",
            sources = listOf(
                Source(clazz = "file", value = "/Shared/assets.zip", displayName = "assets.zip")
            ),
            recipients = listOf(
                Recipient(
                    clazz = "federated",
                    value = "partner@nextcloud.org",
                    instance = "nextcloud.org",
                    displayName = "Partner Cloud",
                    icon = Icon(
                        light = "https://mock/icons/federated_light.png",
                        dark = "https://mock/icons/federated_dark.png"
                    ),
                    secret = Secret(false, value = "", url = "")
                )
            ),
            owner = Owner(
                userId = "partner",
                displayName = "Partner Cloud",
                icon = Icon(
                    light = "https://mock/icons/user_light.png",
                    dark = "https://mock/icons/user_dark.png"
                )
            )
        )
    )

    override suspend fun fetchRecipients(
        recipientTypeClass: String?,
        query: String,
        limit: Int,
        offset: Int
    ): NetworkResult<List<Recipient>> {
        val all = listOf(
            Recipient(
                clazz = "user",
                value = "alice@company.com",
                displayName = "Alice Johnson",
                icon = Icon(
                    light = "https://mock/icons/user_light.png",
                    dark = "https://mock/icons/user_dark.png"
                ),
                secret = Secret(false, value = "", url = "")
            ),
            Recipient(
                clazz = "group",
                value = "marketing",
                displayName = "Marketing Team",
                icon = Icon(
                    light = "https://mock/icons/group_light.png",
                    dark = "https://mock/icons/group_dark.png"
                ),
                secret = Secret(false, value = "", url = "")
            ),
            Recipient(
                clazz = "mail",
                value = "john@external.com",
                displayName = "John External",
                icon = Icon(
                    light = "https://mock/icons/external_light.png",
                    dark = "https://mock/icons/external_dark.png"
                ),
                secret = Secret(false, value = "", url = "")
            ),
            Recipient(
                clazz = "federated",
                value = "partner@nextcloud.org",
                instance = "nextcloud.org",
                displayName = "Partner Cloud",
                icon = Icon(
                    light = "https://mock/icons/federated_light.png",
                    dark = "https://mock/icons/federated_dark.png"
                ),
                secret = Secret(false, value = "", url = "")
            )
        )

        val filtered = all
            .filter { recipientTypeClass == null || it.clazz == recipientTypeClass }
            .filter { it.displayName.contains(query, ignoreCase = true) || it.value.contains(query, ignoreCase = true) }
            .drop(offset)
            .take(limit)

        return NetworkResult.Success(filtered)
    }

    override suspend fun createDraftShare(): NetworkResult<Share> {
        val share = buildShare(
            id = "mock-share-${System.currentTimeMillis()}",
            sources = emptyList(),
            recipients = emptyList(),
            shareState = ShareState.DRAFT
        )
        mockShares.add(share)
        return NetworkResult.Success(share)
    }

    override suspend fun fetchShare(
        id: String,
        request: GetShareRequest
    ): NetworkResult<Share> {
        val share = mockShares.find { it.id == id }
            ?: return NetworkResult.Success(
                buildShare(id = id, sources = emptyList(), recipients = emptyList())
            )
        return NetworkResult.Success(share)
    }

    override suspend fun deleteShare(id: String): NetworkResult<Unit> {
        mockShares.removeAll { it.id == id }
        return NetworkResult.Success(Unit)
    }

    override suspend fun fetchShares(
        sourceClass: String?,
        lastShareID: String?,
        limit: Int
    ): NetworkResult<List<Share>> {
        var result = mockShares.toList()

        if (sourceClass != null) {
            result = result.filter { share -> share.sources.any { it.clazz == sourceClass } }
        }

        if (lastShareID != null) {
            val index = result.indexOfFirst { it.id == lastShareID }
            if (index >= 0) result = result.drop(index + 1)
        }

        return NetworkResult.Success(result.take(limit))
    }

    override suspend fun updateShareState(
        id: String,
        request: UpdateShareStateRequest
    ): NetworkResult<Share> {
        val index = mockShares.indexOfFirst { it.id == id }
        val updated = (if (index >= 0) mockShares[index] else buildShare(id = id, sources = emptyList(), recipients = emptyList()))
            .copy(shareState = request.shareState, lastUpdated = System.currentTimeMillis())
        if (index >= 0) mockShares[index] = updated
        return NetworkResult.Success(updated)
    }

    override suspend fun addShareSource(
        id: String,
        request: AddSourceRequest
    ): NetworkResult<Share> {
        val index = mockShares.indexOfFirst { it.id == id }
        val current = if (index >= 0) mockShares[index] else buildShare(id = id, sources = emptyList(), recipients = emptyList())
        val newSource = Source(clazz = request.clazz, value = request.value, displayName = request.value)
        val updated = current.copy(
            sources = current.sources + newSource,
            lastUpdated = System.currentTimeMillis()
        )
        if (index >= 0) mockShares[index] = updated
        return NetworkResult.Success(updated)
    }

    override suspend fun removeShareSource(
        id: String,
        clazz: String,
        value: String
    ): NetworkResult<Share> {
        val index = mockShares.indexOfFirst { it.id == id }
        val current = if (index >= 0) mockShares[index] else buildShare(id = id, sources = emptyList(), recipients = emptyList())
        val updated = current.copy(
            sources = current.sources.filterNot { it.clazz == clazz && it.value == value },
            lastUpdated = System.currentTimeMillis()
        )
        if (index >= 0) mockShares[index] = updated
        return NetworkResult.Success(updated)
    }

    override suspend fun addShareRecipient(
        id: String,
        request: AddRecipientRequest
    ): NetworkResult<Share> {
        val index = mockShares.indexOfFirst { it.id == id }
        val current = if (index >= 0) mockShares[index] else buildShare(id = id, sources = emptyList(), recipients = emptyList())
        val newRecipient = Recipient(
            clazz = request.clazz,
            value = request.value,
            instance = request.instance,
            displayName = request.value,
            secret = Secret(false, value = "", url = "")
        )
        val updated = current.copy(
            recipients = current.recipients + newRecipient,
            lastUpdated = System.currentTimeMillis()
        )
        if (index >= 0) mockShares[index] = updated
        return NetworkResult.Success(updated)
    }

    override suspend fun removeShareRecipient(
        id: String,
        clazz: String,
        value: String,
        instance: String?
    ): NetworkResult<Share> {
        val index = mockShares.indexOfFirst { it.id == id }
        val current = if (index >= 0) mockShares[index] else buildShare(id = id, sources = emptyList(), recipients = emptyList())
        val updated = current.copy(
            recipients = current.recipients.filterNot {
                it.clazz == clazz && it.value == value && it.instance == instance
            },
            lastUpdated = System.currentTimeMillis()
        )
        if (index >= 0) mockShares[index] = updated
        return NetworkResult.Success(updated)
    }

    override suspend fun updateShareProperty(
        id: String,
        request: UpdateSharePropertyRequest
    ): NetworkResult<Share> {
        val index = mockShares.indexOfFirst { it.id == id }
        val current = if (index >= 0) mockShares[index] else buildShare(id = id, sources = emptyList(), recipients = emptyList())
        val updated = current.copy(lastUpdated = System.currentTimeMillis())
        if (index >= 0) mockShares[index] = updated
        return NetworkResult.Success(updated)
    }

    override suspend fun updateSharePermission(
        id: String,
        request: UpdateSharePermissionRequest
    ): NetworkResult<Share> {
        val index = mockShares.indexOfFirst { it.id == id }
        val current =
            if (index >= 0) mockShares[index] else buildShare(id = id, sources = emptyList(), recipients = emptyList())
        val updatedPermissions = current.permissions.map {
            if (it.clazz == request.clazz) it.copy(enabled = request.enabled) else it
        }
        val updated = current.copy(
            permissions = updatedPermissions,
            lastUpdated = System.currentTimeMillis()
        )
        if (index >= 0) mockShares[index] = updated
        return NetworkResult.Success(updated)
    }
}
