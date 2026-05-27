/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api.request

import com.nextcloud.android.common.ui.share.model.api.state.ShareState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class GetShareRequest(
    val secret: String? = null,
    val arguments: Map<String, JsonElement> = emptyMap()
)

@Serializable
data class UpdateShareStateRequest(
    val shareState: ShareState
)

@Serializable
data class AddSourceRequest(
    @SerialName("class")
    val clazz: String,

    val value: String
)

@Serializable
data class AddRecipientRequest(
    @SerialName("class")
    val clazz: String,

    val value: String,

    val instance: String? = null
)

@Serializable
data class UpdateSharePropertyRequest(
    @SerialName("class")
    val clazz: String,

    val value: String? = null
)

@Serializable
data class UpdateSharePermissionRequest(
    @SerialName("class")
    val clazz: String,

    val enabled: Boolean
)
