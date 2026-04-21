/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.ui

import com.nextcloud.android.common.ui.share.model.api.owner.Owner
import com.nextcloud.android.common.ui.share.model.api.user.ShareUser

data class UnifiedShare(
    val id: String?,
    val sources: List<ShareUser>,
    val recipients: List<ShareUser>,
    val properties: Map<String, Map<String, List<String>>>,

    val lastUpdated: Long,
    val owner: Owner?,

    val permission: UnifiedSharePermission?,
    val type: UnifiedShareType?,
    val category: UnifiedShareCategory,
    val label: String,
    val note: String = "",
    val password: String = "",
    val limit: UnifiedShareDownloadLimit? = null
) {
    companion object {
        fun new(): UnifiedShare {
            return UnifiedShare(
                id = null,
                sources = listOf(),
                recipients = listOf(),
                properties = mapOf(),
                lastUpdated = -1,
                owner = null,
                permission = null,
                type = null,
                category = UnifiedShareCategory.Invited,
                label = "",
                note = "",
                password = "",
                limit = null
            )
        }
    }
}
