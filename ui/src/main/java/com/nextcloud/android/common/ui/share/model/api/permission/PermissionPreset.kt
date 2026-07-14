/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api.permission

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PermissionPreset(
    @SerialName("class")
    val clazz: String,

    @SerialName("display_name")
    val displayName: String,

    val hint: String? = null
)
