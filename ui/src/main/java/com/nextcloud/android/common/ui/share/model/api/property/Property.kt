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
sealed class Property {
    abstract val clazz: String
    abstract val displayName: String
    abstract val hint: String?
    abstract val priority: Int
    abstract val required: Boolean
    abstract val value: String?
}

@Serializable
@SerialName("boolean")
data class PropertyBoolean(
    @SerialName("class") override val clazz: String,
    @SerialName("display_name") override val displayName: String,
    override val hint: String? = null,
    override val priority: Int,
    override val required: Boolean,
    override val value: String? = null
) : Property() {
    fun isTrue(): Boolean = value == "true"
}

@Serializable
@SerialName("date")
data class PropertyDate(
    @SerialName("class") override val clazz: String,
    @SerialName("display_name") override val displayName: String,
    override val hint: String? = null,
    override val priority: Int,
    override val required: Boolean,
    override val value: String? = null,
    @SerialName("min_date") val minDate: String? = null,
    @SerialName("max_date") val maxDate: String? = null
) : Property()

@Serializable
@SerialName("enum")
data class PropertyEnum(
    @SerialName("class") override val clazz: String,
    @SerialName("display_name") override val displayName: String,
    override val hint: String? = null,
    override val priority: Int,
    override val required: Boolean,
    override val value: String? = null,
    @SerialName("valid_values") val validValues: List<String>
) : Property()

@Serializable
@SerialName("password")
data class PropertyPassword(
    @SerialName("class") override val clazz: String,
    @SerialName("display_name") override val displayName: String,
    override val hint: String? = null,
    override val priority: Int,
    override val required: Boolean,
    override val value: String? = null
) : Property()

@Serializable
@SerialName("string")
data class PropertyString(
    @SerialName("class") override val clazz: String,
    @SerialName("display_name") override val displayName: String,
    override val hint: String? = null,
    override val priority: Int,
    override val required: Boolean,
    override val value: String? = null,
    @SerialName("min_length") val minLength: Int? = null,
    @SerialName("max_length") val maxLength: Int? = null
) : Property()
