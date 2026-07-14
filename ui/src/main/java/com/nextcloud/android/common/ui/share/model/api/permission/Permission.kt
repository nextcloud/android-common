/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api.permission

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [presets] holds the class identifiers of the [PermissionPreset]s this permission belongs to.
 */
@Serializable
data class Permission(
    @SerialName("class")
    val clazz: String,

    @SerialName("source_class")
    val sourceClass: String? = null,

    @SerialName("display_name")
    val displayName: String,

    val hint: String? = null,

    val priority: Int,

    val presets: List<String>,

    val enabled: Boolean
)
