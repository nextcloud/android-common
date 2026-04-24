/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.ui

sealed class ShareBottomSheetState {
    data object Idle: ShareBottomSheetState()
    data class New(val newShare: UnifiedShare): ShareBottomSheetState()
    data class Edit(val share: UnifiedShare): ShareBottomSheetState()
}
