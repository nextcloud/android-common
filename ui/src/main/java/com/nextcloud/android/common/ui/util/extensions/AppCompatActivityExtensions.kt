/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2025 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.util.extensions

import android.graphics.Color
import android.os.Build
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

@JvmOverloads
@Suppress("MagicNumber")
fun AppCompatActivity.adjustUIForAPILevel35(
    statusBarStyle: SystemBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
    navigationBarStyle: SystemBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
) {
    val isApiLevel35OrHigher = (Build.VERSION.SDK_INT >= 35)
    if (!isApiLevel35OrHigher) {
        return
    }

    enableEdgeToEdge(statusBarStyle, navigationBarStyle)

    window.addSystemBarPaddings()
}
