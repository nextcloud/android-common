/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api.recipients

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShareRecipients(
    val type: String,
    val value: String,

    @SerialName("display_name")
    val displayName: String,

    @SerialName("display_name_unique")
    val displayNameUnique: String,

    @SerialName("icon_url_light")
    val iconUrlLight: String,

    @SerialName("icon_url_dark")
    val iconUrlDark: String
)