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

/**
 *
 * This function provides a unified approach to setting status bar colors across different
 * Android versions, with special handling for Android 15 (VANILLA_ICE_CREAM) and above
 * where direct status bar color modification is no longer supported.
 *
 * ## Android Version Compatibility:
 *
 * ### Android 14 and below (API < 35):
 * - Uses the traditional `Window.setStatusBarColor()` method
 *
 * ### Android 15+ (API 35+):
 * - **⚠ IMPORTANT**: Direct status bar color modification is NOT possible
 * - Uses a workaround by applying top padding equal to status bar height
 * - Sets the view's background color to simulate the desired appearance
 * - This is a visual approximation, not actual status bar color change
 *
 *  * @see [Android Documentation](https://developer.android.com/reference/kotlin/android/view/Window#setstatusbarcolor)
 *  * @see Window.setStatusBarColor (deprecated in API 35)
 *
 * @param color The desired color as a ColorInt.
 *
 * @warning On Android 15+, this is a visual workaround only. The actual status bar
 *          color cannot be modified and will remain transparent with system-managed contrast.
 */
fun AppCompatActivity.initStatusBar(
    @ColorInt color: Int
) {
    window.decorView.setOnApplyWindowInsetsListener { view, insets ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            val statusBarHeight = insets.getInsets(WindowInsets.Type.statusBars()).top
            view.setPadding(0, statusBarHeight, 0, 0)
            view.setBackgroundColor(color)
        } else {
            @Suppress("DEPRECATION")
            window.statusBarColor = color
        }

        insets
    }
}
