/*
 * Nextcloud - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Alper Ozturk <alper.ozturk@nextcloud.com>
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */

package com.nextcloud.android.common.ui.share.model.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

enum class ShareItemType {
    Top, Mid, Bottom;

    @Composable
    fun getShape(): RoundedCornerShape {
        return when (this) {
            Top -> RoundedCornerShape(12.dp, 12.dp, 4.dp, 4.dp)
            Mid -> RoundedCornerShape(4.dp, 4.dp, 4.dp, 4.dp)
            Bottom -> RoundedCornerShape(4.dp, 4.dp, 12.dp, 12.dp)
        }
    }
}
