/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api.property

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("type")
@Serializable
sealed class Property

@Serializable
@SerialName("boolean")
data class PropertyBoolean(
    @SerialName("class")
    val clazz: String,

    @SerialName("display_name")
    val displayName: String,

    val hint: String? = null,

    val priority: Int,

    val required: Boolean,

    val value: String? = null
) : Property()

@Serializable
@SerialName("date")
data class PropertyDate(
    @SerialName("class")
    val clazz: String,

    @SerialName("display_name")
    val displayName: String,

    val hint: String? = null,

    val priority: Int,

    val required: Boolean,

    val value: String? = null,

    @SerialName("min_date")
    val minDate: String? = null,

    @SerialName("max_date")
    val maxDate: String? = null
) : Property()

@Serializable
@SerialName("enum")
data class PropertyEnum(
    @SerialName("class")
    val clazz: String,

    @SerialName("display_name")
    val displayName: String,

    val hint: String? = null,

    val priority: Int,

    val required: Boolean,

    val value: String? = null,

    @SerialName("valid_values")
    val validValues: List<String>
) : Property()

@Serializable
@SerialName("password")
data class PropertyPassword(
    @SerialName("class")
    val clazz: String,

    @SerialName("display_name")
    val displayName: String,

    val hint: String? = null,

    val priority: Int,

    val required: Boolean,

    val value: String? = null
) : Property()

@Serializable
@SerialName("string")
data class PropertyString(
    @SerialName("class")
    val clazz: String,

    @SerialName("display_name")
    val displayName: String,

    val hint: String? = null,

    val priority: Int,

    val required: Boolean,

    val value: String? = null,

    @SerialName("min_length")
    val minLength: Int? = null,

    @SerialName("max_length")
    val maxLength: Int? = null
) : Property()
