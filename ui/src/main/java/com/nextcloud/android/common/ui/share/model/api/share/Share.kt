/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api.share

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

    val shareState: ShareState,

    val sources: List<Source>,

    val recipients: List<Recipient>,

    val properties: List<Property>,

    val permissions: List<Permission>
)
