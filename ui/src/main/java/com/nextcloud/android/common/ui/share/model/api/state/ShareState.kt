/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ShareState {
    @SerialName("active")
    ACTIVE,

    @SerialName("draft")
    DRAFT,

    @SerialName("deleted")
    DELETED
}
