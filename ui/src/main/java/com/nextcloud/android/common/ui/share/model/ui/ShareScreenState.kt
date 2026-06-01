/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.ui

import com.nextcloud.android.common.ui.share.model.api.share.Share

sealed class ShareScreenState {
    data object Empty: ShareScreenState()
    data object Loading: ShareScreenState()
    data class Loaded(val shares: List<Share>): ShareScreenState()
}
