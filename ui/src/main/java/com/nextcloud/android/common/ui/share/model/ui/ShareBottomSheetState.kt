/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.ui

import com.nextcloud.android.common.ui.share.model.api.share.Share

sealed class ShareBottomSheetState {
    data object Idle: ShareBottomSheetState()
    data class New(val newShare: Share): ShareBottomSheetState()
    data class Edit(val share: Share): ShareBottomSheetState()
}
