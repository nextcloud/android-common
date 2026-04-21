/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api.create

import com.nextcloud.android.common.ui.share.model.api.user.ShareUser
import kotlinx.serialization.Serializable

@Serializable
data class CreateShareRequest(
    val data: ShareDataRequest
)

@Serializable
data class ShareDataRequest(
    val sources: List<ShareUser>,
    val recipients: List<ShareUser>,
    val properties: Map<String, Map<String, List<String>>>
)
