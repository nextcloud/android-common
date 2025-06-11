/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2025 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.util.extensions

import android.graphics.Color
import android.os.Build
import android.view.WindowInsets
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorInt
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

fun AppCompatActivity.initStatusBar(
    @ColorInt color: Int
) {
    window.decorView.setOnApplyWindowInsetsListener { view, insets ->
        view.setBackgroundColor(color)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            val statusBarHeight = insets.getInsets(WindowInsets.Type.statusBars()).top
            view.setPadding(0, statusBarHeight, 0, 0)
        }

        insets
    }
}
