/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api.capabilities

import com.nextcloud.android.common.ui.network.serialization.OCSSerializer

object SharingCapabilitiesParser {
    fun parse(sharingJson: String?): SharingCapabilities? {
        if (sharingJson.isNullOrEmpty()) return null
        return runCatching {
            OCSSerializer.json.decodeFromString<SharingCapabilities>(sharingJson)
        }.getOrNull()
    }
}
