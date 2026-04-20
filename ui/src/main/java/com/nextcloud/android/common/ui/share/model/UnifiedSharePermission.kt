/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model

sealed class UnifiedSharePermission {
    // file drop only for folder
    data object FileDrop : UnifiedSharePermission()

    data object CanView : UnifiedSharePermission()
    data object CanEdit : UnifiedSharePermission()

    // create only for folder
    data class Custom(val read: Boolean, val edit: Boolean, val delete: Boolean, val create: Boolean) :
        UnifiedSharePermission()

    fun getText(): String {
        return when(this) {
            FileDrop -> "File drop"
            CanView -> "Can view"
            CanEdit -> "Can edit"
            is Custom -> "Custom permissions"
        }
    }
}

