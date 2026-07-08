/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.ui

import com.nextcloud.android.common.ui.share.model.api.share.Share

sealed class ActiveShareState {
    data object Dismiss: ActiveShareState()
    data class Update(val value: Share): ActiveShareState()
    data class SharedAndDismiss(val value: Share): ActiveShareState()
}
