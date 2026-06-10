/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api.capabilities

import com.nextcloud.android.common.ui.share.model.api.icon.Icon
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PermissionCategoryType(
    @SerialName("class")
    val classField: String,
    @SerialName("display_name")
    val displayName: String,
    val hint: String? = null,
    val icon: Icon? = null,
    val priority: Long,
)
