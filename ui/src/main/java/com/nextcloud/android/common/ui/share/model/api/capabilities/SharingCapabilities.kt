/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api.capabilities

import com.nextcloud.android.common.ui.share.model.api.permission.PermissionPreset
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SharingCapabilities(
    @SerialName("api_versions")
    val apiVersions: List<String> = emptyList(),
    val legacy: Legacy? = null,
    @SerialName("source_types")
    val sourceTypes: List<SourceType> = emptyList(),
    @SerialName("permission_presets")
    val permissionPresets: List<PermissionPreset> = emptyList(),
)
