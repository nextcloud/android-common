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
import scheme.Scheme

internal class MaterialSchemesImpl :
    MaterialSchemes {
    override val lightScheme: Scheme
    override val darkScheme: Scheme

    constructor(@ColorInt primaryColor: Int, primaryAsBackground: Boolean = false) {
        if (primaryAsBackground) {
            val scheme = primaryAsBackgroundScheme(primaryColor)
            darkScheme = scheme
            lightScheme = scheme
        } else {
            lightScheme = Scheme.light(primaryColor)
            darkScheme = Scheme.dark(primaryColor)
        }
    }

    private fun primaryAsBackgroundScheme(primaryColor: Int): Scheme {
        val hct = Hct.fromInt(primaryColor)
        val isDark = hct.tone < DARK_TONE_THRESHOLD
        val scheme = if (isDark) {
            Scheme.lightContent(primaryColor).withPrimary(primaryColor)
        } else {
            Scheme.darkContent(primaryColor).withPrimary(primaryColor)
        }
        return scheme
    }

    constructor(serverTheme: ServerTheme) : this(serverTheme.primaryColor)

    companion object {
        // chosen from material-color-utils default tone for dark backgrounds
        private const val DARK_TONE_THRESHOLD = 80.0
    }
}
