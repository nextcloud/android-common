/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api.user

import com.nextcloud.android.common.ui.share.model.api.icon.Icon
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("user_id")
    val userId: String,

    val instance: String? = null,

    @SerialName("display_name")
    val displayName: String,

    val icon: Icon
)
