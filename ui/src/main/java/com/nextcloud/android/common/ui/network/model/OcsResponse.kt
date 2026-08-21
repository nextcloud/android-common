/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OcsResponse<T>(
    val ocs: Ocs<T>
)

@Serializable
data class Ocs<T>(
    val meta: Meta,
    val data: T
)

@Serializable
data class Meta(
    val status: String,

    @SerialName("statuscode")
    val statusCode: Int,

    val message: String,

    @SerialName("totalitems")
    val totalItems: String = "",

    @SerialName("itemsperpage")
    val itemsPerPage: String = ""
)
