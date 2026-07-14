/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.ui

import com.nextcloud.android.common.ui.share.model.api.share.Share

sealed class ActiveShareState {
    data object None : ActiveShareState()
    data class Editing(val share: Share) : ActiveShareState()
    data class Activating(val share: Share) : ActiveShareState()

    val shareOrNull: Share?
        get() = when (this) {
            is Editing -> share
            is Activating -> share
            None -> null
        }
}
