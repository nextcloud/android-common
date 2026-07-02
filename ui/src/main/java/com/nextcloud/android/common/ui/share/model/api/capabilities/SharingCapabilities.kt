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
    val legacy: Legacy? = null,
    @SerialName("source_types")
    val sourceTypes: List<SourceType>,
)
