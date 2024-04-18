/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2022-2023 Nextcloud GmbH and Nextcloud contributors
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-License-Identifier: MIT
 */
package com.nextcloud.android.common.ui.theme.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import com.nextcloud.android.common.ui.R
import dynamiccolor.DynamicScheme
import dynamiccolor.MaterialDynamicColors

/**
 * To be used to calculate color lists for both Switch and SwitchCompat
 */
internal object SwitchColorUtils {
    private const val SWITCH_COMPAT_TRACK_ALPHA: Int = 77

    data class SwitchColors(
        val thumbColor: ColorStateList,
        val trackColor: ColorStateList
    )

    fun calculateSwitchColors(
        context: Context,
        scheme: DynamicScheme
    ): SwitchColors {
        val dynamicColor = MaterialDynamicColors()

        val thumbUncheckedColor =
            ResourcesCompat.getColor(
                context.resources,
                R.color.switch_thumb_color_unchecked,
                context.theme
            )
        val trackUncheckedColor =
            ResourcesCompat.getColor(
                context.resources,
                R.color.switch_track_color_unchecked,
                context.theme
            )

        val trackColor =
            Color.argb(
                SWITCH_COMPAT_TRACK_ALPHA,
                Color.red(dynamicColor.primary().getArgb(scheme)),
                Color.green(dynamicColor.primary().getArgb(scheme)),
                Color.blue(dynamicColor.primary().getArgb(scheme))
            )

        return SwitchColors(
            thumbColor =
                ColorStateList(
                    arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                    intArrayOf(dynamicColor.primary().getArgb(scheme), thumbUncheckedColor)
                ),
            trackColor =
                ColorStateList(
                    arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                    intArrayOf(trackColor, trackUncheckedColor)
                )
        )
    }
}
