/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val OUTER_CORNER_RADIUS = 12.dp
private val INNER_CORNER_RADIUS = 4.dp

enum class ShareItemType {
    Single,
    Top,
    Mid,
    Bottom;

    @Composable
    fun getShape(): RoundedCornerShape = when (this) {
        Single -> {
            RoundedCornerShape(OUTER_CORNER_RADIUS)
        }

        Top -> {
            RoundedCornerShape(
                OUTER_CORNER_RADIUS,
                OUTER_CORNER_RADIUS,
                INNER_CORNER_RADIUS,
                INNER_CORNER_RADIUS
            )
        }

        Mid -> {
            RoundedCornerShape(INNER_CORNER_RADIUS)
        }

        Bottom -> {
            RoundedCornerShape(
                INNER_CORNER_RADIUS,
                INNER_CORNER_RADIUS,
                OUTER_CORNER_RADIUS,
                OUTER_CORNER_RADIUS
            )
        }
    }

    companion object {
        fun type(index: Int, lastIndex: Int): ShareItemType = when {
            lastIndex == 0 -> Single
            index == 0 -> Top
            index == lastIndex -> Bottom
            else -> Mid
        }
    }
}
