/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api.source

import com.nextcloud.android.common.ui.share.model.api.icon.Icon
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Source(
    @SerialName("class")
    val clazz: String,

    val value: String,

    @SerialName("display_name")
    val displayName: String,

    val icon: Icon? = null
)
