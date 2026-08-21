/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.ui

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver

sealed class ShareOverlay {
    abstract val shareId: String?

    data object None : ShareOverlay() {
        override val shareId: String? = null
    }

    data class QuickShare(override val shareId: String) : ShareOverlay()
    data class DeleteConfirmation(override val shareId: String) : ShareOverlay()

    companion object {
        private const val QUICK_SHARE = "quick_share"
        private const val DELETE_CONFIRMATION = "delete_confirmation"

        val Saver: Saver<ShareOverlay, Any> = listSaver(
            save = { overlay ->
                when (overlay) {
                    None -> emptyList()
                    is QuickShare -> listOf(QUICK_SHARE, overlay.shareId)
                    is DeleteConfirmation -> listOf(DELETE_CONFIRMATION, overlay.shareId)
                }
            },
            restore = { saved ->
                val shareId = saved.getOrNull(1)
                when (saved.firstOrNull()) {
                    QUICK_SHARE -> shareId?.let(::QuickShare)
                    DELETE_CONFIRMATION -> shareId?.let(::DeleteConfirmation)
                    else -> None
                } ?: None
            }
        )
    }
}
