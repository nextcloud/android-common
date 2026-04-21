/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api.update

import com.nextcloud.android.common.ui.share.model.api.owner.Owner
import com.nextcloud.android.common.ui.share.model.api.user.ShareUser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateShareRequest(
    val data: UpdateShareData
)

@Serializable
data class UpdateShareData(
    val sources: List<ShareUser>,
    val recipients: List<ShareUser>,
    val properties: Map<String, Map<String, List<String>>>,

    val id: String,

    @SerialName("last_updated")
    val lastUpdated: Long,

    val owner: Owner
)
