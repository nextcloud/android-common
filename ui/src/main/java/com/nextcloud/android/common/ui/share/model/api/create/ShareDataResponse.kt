/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api.create

import com.nextcloud.android.common.ui.share.model.api.owner.Owner
import com.nextcloud.android.common.ui.share.model.api.user.ShareUser
import com.nextcloud.android.common.ui.share.model.ui.UnifiedShare
import com.nextcloud.android.common.ui.share.model.ui.UnifiedShareCategory
import com.nextcloud.android.common.ui.share.model.ui.UnifiedSharePermission
import com.nextcloud.android.common.ui.share.model.ui.UnifiedShareType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShareDataResponse(
    val sources: List<ShareUser>,
    val recipients: List<ShareUser>,
    val properties: Map<String, Map<String, List<String>>>,

    val id: String,

    @SerialName("last_updated")
    val lastUpdated: Long,

    val owner: Owner
)

fun ShareDataResponse.toUnifiedShare(): UnifiedShare {
    val primarySource = sources.firstOrNull()
    return UnifiedShare(
        id = id,
        sources = sources,
        recipients = recipients,
        properties = properties,
        lastUpdated = lastUpdated,
        owner = owner,
        type = UnifiedShareType.toUnifiedShareType(primarySource?.type),
        category = UnifiedShareCategory.Invited, // TODO map from properties
        permission = UnifiedSharePermission.CanView, // TODO map from properties
        label = primarySource?.displayName ?: "Unknown",
        note = "", // TODO map from properties
        password = "", // TODO map from properties
        limit = null // TODO map from properties
    )
}
