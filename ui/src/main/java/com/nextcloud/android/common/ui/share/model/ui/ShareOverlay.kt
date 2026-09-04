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

    data class RecipientPermission(
        override val shareId: String,
        val recipientClass: String,
        val recipientValue: String,
        val recipientInstance: String?
    ) : ShareOverlay()

    companion object {
        private const val QUICK_SHARE = "quick_share"
        private const val RECIPIENT_PERMISSION = "recipient_permission"
        private const val RECIPIENT_CLASS_INDEX = 2
        private const val RECIPIENT_VALUE_INDEX = 3
        private const val RECIPIENT_INSTANCE_INDEX = 4

        val Saver: Saver<ShareOverlay, Any> =
            listSaver(
                save = { overlay ->
                    when (overlay) {
                        None -> {
                            emptyList()
                        }

                        is QuickShare -> {
                            listOf(QUICK_SHARE, overlay.shareId)
                        }

                        is RecipientPermission -> {
                            listOf(
                                RECIPIENT_PERMISSION,
                                overlay.shareId,
                                overlay.recipientClass,
                                overlay.recipientValue,
                                overlay.recipientInstance.orEmpty()
                            )
                        }
                    }
                },
                restore = { saved ->
                    val shareId = saved.getOrNull(1)
                    when (saved.firstOrNull()) {
                        QUICK_SHARE -> {
                            shareId?.let(::QuickShare)
                        }

                        RECIPIENT_PERMISSION -> {
                            shareId?.let {
                                RecipientPermission(
                                    shareId = it,
                                    recipientClass = saved[RECIPIENT_CLASS_INDEX],
                                    recipientValue = saved[RECIPIENT_VALUE_INDEX],
                                    recipientInstance = saved[RECIPIENT_INSTANCE_INDEX].takeIf(String::isNotEmpty)
                                )
                            }
                        }

                        else -> {
                            None
                        }
                    } ?: None
                }
            )
    }
}
