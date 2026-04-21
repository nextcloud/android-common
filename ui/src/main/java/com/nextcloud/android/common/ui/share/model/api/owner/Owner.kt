/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api.owner

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Owner(
    @SerialName("user_id")
    val userId: String,

    @SerialName("display_name")
    val displayName: String
)
