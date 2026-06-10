/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api.secret

import kotlinx.serialization.Serializable

@Serializable
data class Secret(
    val updatable: Boolean,
    val value: String?,
    val url: String?
)
