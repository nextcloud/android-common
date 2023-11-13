/*
 * Nextcloud Android Common Library
 *
 * @author Andy Scherzinger
 * Copyright (C) 2022 Andy Scherzinger <info@andy-scherzinger.de>
 * Copyright (C) 2022 Nextcloud GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.nextcloud.android.common.ui.theme

import androidx.annotation.ColorInt
import hct.Hct
import scheme.DynamicScheme
import scheme.SchemeContent
import scheme.SchemeTonalSpot


internal class MaterialSchemesImpl :
    MaterialSchemes {
    override val lightScheme: DynamicScheme
    override val darkScheme: DynamicScheme

    constructor(
        @ColorInt primaryColor: Int,
        primaryAsBackground: Boolean = false
    ) {
        if (primaryAsBackground) {
            val scheme = primaryAsBackgroundScheme(primaryColor)
            darkScheme = scheme
            lightScheme = scheme
        } else {
            lightScheme = SchemeTonalSpot(Hct.fromInt(primaryColor), false, 0.0)
            darkScheme = SchemeTonalSpot(Hct.fromInt(primaryColor), true, 0.0)
        }
    }

    private fun primaryAsBackgroundScheme(primaryColor: Int): DynamicScheme {
        val hct = Hct.fromInt(primaryColor)
        val isDark = hct.tone < DARK_TONE_THRESHOLD
        return SchemeContent(Hct.fromInt(primaryColor), !isDark, 0.0)
    }

    constructor(serverTheme: ServerTheme) : this(serverTheme.primaryColor)

    companion object {
        // chosen from material-color-utils default tone for dark backgrounds
        private const val DARK_TONE_THRESHOLD = 80.0
    }
}
