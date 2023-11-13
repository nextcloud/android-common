/*
 * Nextcloud Android client application
 *
 *  @author Álvaro Brey
 *  Copyright (C) 2022 Álvaro Brey
 *  Copyright (C) 2022 Nextcloud GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU AFFERO GENERAL PUBLIC LICENSE
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU AFFERO GENERAL PUBLIC LICENSE for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.nextcloud.android.common.ui.theme.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import com.nextcloud.android.common.ui.R
import dynamiccolor.MaterialDynamicColors
import scheme.DynamicScheme
import scheme.Scheme

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
