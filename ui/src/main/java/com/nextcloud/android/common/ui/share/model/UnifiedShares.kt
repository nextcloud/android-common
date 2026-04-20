/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model

data class UnifiedShares(
    val id: Int,
    val password: String,
    val note: String,
    val limit: UnifiedShareDownloadLimit,
    val expirationDate: Int,
    val permission: UnifiedSharePermission,
    val label: String,
    val sharedTo: String,
    val type: UnifiedShareType,
    val category: UnifiedShareCategory,
)
