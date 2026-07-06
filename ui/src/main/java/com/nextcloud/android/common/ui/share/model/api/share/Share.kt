/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api.share

import android.content.ClipData
import android.content.Context
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.toClipEntry
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.share.model.api.permission.Permission
import com.nextcloud.android.common.ui.share.model.api.permission.PermissionPreset
import com.nextcloud.android.common.ui.share.model.api.property.Property
import com.nextcloud.android.common.ui.share.model.api.recipients.Recipient
import com.nextcloud.android.common.ui.share.model.api.source.Source
import com.nextcloud.android.common.ui.share.model.api.state.ShareState
import com.nextcloud.android.common.ui.share.model.api.user.User
import com.nextcloud.android.common.ui.share.model.ui.ShareCategory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Share(
    val id: String,

    val owner: User,

    @SerialName("last_updated")
    val lastUpdated: Long,

    @SerialName("state")
    val shareState: ShareState,

    val sources: List<Source>,

    val recipients: List<Recipient>,

    val properties: List<Property>,

    val permissions: List<Permission>,

    @SerialName("permission_preset")
    val permissionPreset: PermissionPreset? = null
) {
    fun getClipEntry(category: ShareCategory, internalLink: String): ClipEntry? {
        val recipient = recipients.firstOrNull() ?: return null

        return if (category == ShareCategory.Anyone) {
            val link = recipient.secret.url ?: return null
            ClipData.newPlainText(recipient.displayName, link).toClipEntry()
        } else {
            ClipData.newPlainText(recipient.displayName, internalLink).toClipEntry()
        }
    }

    fun getCustomLinkRecipient(category: ShareCategory): Recipient? {
        return if (category == ShareCategory.Anyone) {
            recipients.firstOrNull { it.secret.updatable }
        } else {
            val firstRecipient = recipients.firstOrNull()
            firstRecipient?.copy(clazz = Recipient.TOKEN_RECIPIENT_CLASS)
        }
    }

    val canSend: Boolean
        get() {
            return sources.isNotEmpty() &&
                recipients.isNotEmpty() &&
                permissions.any { it.enabled } &&
                properties.none { it.required && it.value.isNullOrEmpty() }
        }

    fun title(context: Context): String {
        return if (shareState == ShareState.DRAFT) {
            context.getString(R.string.share_view_bottom_sheet_new_title)
        } else {
            context.getString(R.string.share_view_bottom_sheet_edit_title, sources.firstOrNull()?.displayName)
        }
    }
}
