/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2025 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.util.extensions

import android.view.View
import android.view.Window
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

fun Window?.addSystemBarPaddings() {
    if (this == null) {
        return
    }

    ViewCompat.setOnApplyWindowInsetsListener(decorView) { v: View, insets: WindowInsetsCompat ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

        v.updatePadding(
            left = bars.left,
            top = bars.top,
            right = bars.right,
            bottom = bars.bottom
        )

        WindowInsetsCompat.CONSUMED
    }
}
