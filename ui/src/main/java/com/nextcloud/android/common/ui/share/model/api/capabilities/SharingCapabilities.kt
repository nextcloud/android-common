/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api.capabilities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SharingCapabilities(
    @SerialName("api_versions")
    val apiVersions: List<String>,
    val legacy: Legacy,
    @SerialName("permission_categories")
    val permissionCategories: List<PermissionCategory>,
)

@Serializable
data class Legacy(
    @SerialName("max_sources")
    val maxSources: Long,
    @SerialName("max_recipients")
    val maxRecipients: Long,
)

@Serializable
data class PermissionCategory(
    @SerialName("class")
    val class_field: String,
    @SerialName("display_name")
    val displayName: String,
    val hint: String?,
    val icon: String?,
    val priority: Long,
)
