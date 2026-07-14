/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api.icon

import kotlinx.serialization.Serializable

@Serializable
data class Icon(
    val svg: String? = null,
    val light: String? = null,
    val dark: String? = null
)
