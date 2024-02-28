/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2022-2023 Nextcloud GmbH and Nextcloud contributors
 * SPDX-FileCopyrightText: 2022-2023 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.nextcloud.android.common.ui.color

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.nextcloud.android.common.ui.R
import javax.inject.Inject
import kotlin.math.roundToInt

class ColorUtil
    @Inject
    constructor(private val context: Context) {
        @ColorInt
        fun getNullSafeColor(
            color: String?,
            @ColorInt fallbackColor: Int
        ): Int {
            return color.parseColorOrFallback { fallbackColor }
        }

        @ColorInt
        fun getNullSafeColorWithFallbackRes(
            color: String?,
            @ColorRes fallbackColorRes: Int
        ): Int {
            return color.parseColorOrFallback { ContextCompat.getColor(context, fallbackColorRes) }
        }

        @ColorInt
        fun getTextColor(
            colorText: String?,
            @ColorInt backgroundColor: Int
        ): Int {
            return colorText.parseColorOrFallback { getForegroundColorForBackgroundColor(backgroundColor) }
        }

        @ColorInt
        fun getForegroundColorForBackgroundColor(
            @ColorInt color: Int
        ): Int {
            return if (isDarkBackground(color)) {
                Color.WHITE
            } else {
                ContextCompat.getColor(context, R.color.grey_900)
            }
        }

        fun isDarkBackground(
            @ColorInt color: Int
        ): Boolean {
            val hsl = FloatArray(HSL_SIZE)
            ColorUtils.RGBToHSL(Color.red(color), Color.green(color), Color.blue(color), hsl)

            return hsl[INDEX_LIGHTNESS] < LIGHTNESS_DARK_THRESHOLD
        }

        fun setLightness(
            @ColorInt color: Int,
            lightness: Float
        ): Int {
            require(lightness in 0.0..1.0) { "Lightness must be between 0 and 1" }
            val hsl = FloatArray(HSL_SIZE)
            ColorUtils.RGBToHSL(Color.red(color), Color.green(color), Color.blue(color), hsl)

            hsl[INDEX_LIGHTNESS] = lightness

            return ColorUtils.HSLToColor(hsl)
        }

        fun colorToHexString(
            @ColorInt color: Int
        ): String {
            return String.format(null, "#%06X", HEX_WHITE and color)
        }

        @ColorInt
        private fun String?.parseColorOrFallback(fallback: () -> Int): Int {
            return if (this?.isNotBlank() == true) {
                try {
                    Color.parseColor(this)
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid color: $this", e)
                    fallback()
                }
            } else {
                fallback()
            }
        }

        @ColorInt
        fun adjustOpacity(
            color: Int,
            opacity: Float
        ): Int {
            return Color.argb(
                (Color.alpha(color) * opacity).roundToInt(),
                Color.red(color),
                Color.green(color),
                Color.blue(color)
            )
        }

        companion object {
            private const val HSL_SIZE: Int = 3
            private const val INDEX_LIGHTNESS: Int = 2
            private const val LIGHTNESS_DARK_THRESHOLD: Float = 0.6f
            private const val HEX_WHITE = 0xFFFFFF
            private val TAG = ColorUtil::class.simpleName
        }
    }
