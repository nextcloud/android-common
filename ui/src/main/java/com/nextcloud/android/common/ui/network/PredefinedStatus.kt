/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.network

import kotlinx.serialization.Serializable

@Serializable
data class ClearAt(
    val type: String,
    val time: Int
)

@Serializable
data class PredefinedStatus(
    val id: String,
    val icon: String,
    val message: String,
    val clearAt: ClearAt? = null
)
