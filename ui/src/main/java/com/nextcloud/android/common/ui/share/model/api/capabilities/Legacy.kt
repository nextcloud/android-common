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
data class Legacy(
    @SerialName("max_sources")
    val maxSources: Long,
    @SerialName("max_recipients")
    val maxRecipients: Long,
)
