/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */

package com.nextcloud.android.common.ui.share.model.ui

sealed class ShareItemOverlayState {
    data object None : ShareItemOverlayState()
    data object ContextMenu : ShareItemOverlayState()
    data object DeleteConfirmation : ShareItemOverlayState()
}
