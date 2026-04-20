/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.repository

import com.nextcloud.android.common.ui.share.model.UnifiedShares

interface UnifiedShareRepository {
    suspend fun fetchShares(): List<UnifiedShares>
}
