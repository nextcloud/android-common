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
import com.nextcloud.android.common.ui.share.model.api.owner.Owner
import com.nextcloud.android.common.ui.share.model.api.permission.Permission
import com.nextcloud.android.common.ui.share.model.api.property.Property
import com.nextcloud.android.common.ui.share.model.api.recipients.Recipient
import com.nextcloud.android.common.ui.share.model.api.source.Source
import com.nextcloud.android.common.ui.share.model.api.state.ShareState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Share(
    val id: String,

    val owner: Owner,

    @SerialName("last_updated")
    val lastUpdated: Long,

    @SerialName("state")
    val shareState: ShareState,

    val sources: List<Source>,

    val recipients: List<Recipient>,

    val properties: List<Property>,

    val permissions: List<Permission>
) {
    fun getClipEntry(): ClipEntry? {
        val label = recipients.first().displayName
        val link = recipients.first().secret.url

        return if (link != null) {
            ClipData.newPlainText(label, link).toClipEntry()
        } else {
            null
        }
    }

    fun readyToSend(): Boolean {
        return sources.isNotEmpty() && recipients.isNotEmpty() && permissions.isNotEmpty()
    }

    fun title(context: Context): String {
        return if (shareState == ShareState.DRAFT) {
            context.getString(R.string.share_view_bottom_sheet_new_title)
        } else {
            // TODO do not hardcode
            context.getString(R.string.share_view_bottom_sheet_edit_title, "")
        }
    }
}
