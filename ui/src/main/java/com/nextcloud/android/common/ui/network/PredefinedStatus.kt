/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.network

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive

object ClearAtTimeSerializer : KSerializer<String> {
    override val descriptor = PrimitiveSerialDescriptor("ClearAtTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String {
        val jsonDecoder = decoder as? JsonDecoder ?: return decoder.decodeString()
        return (jsonDecoder.decodeJsonElement() as? JsonPrimitive)?.content ?: ""
    }

    override fun serialize(encoder: Encoder, value: String) = encoder.encodeString(value)
}

@Serializable
data class ClearAt(
    val type: String,
    @Serializable(with = ClearAtTimeSerializer::class)
    val time: String
)

@Serializable
data class PredefinedStatus(
    val id: String,
    val icon: String,
    val message: String,
    val clearAt: ClearAt? = null
)
