/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.network.serialization

import kotlinx.serialization.json.Json

object OCSSerializer {
    val json = Json { ignoreUnknownKeys = true }
}
