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
import scheme.DynamicScheme

interface MaterialSchemes {
    /**
     * Schema for light theme
     */
    val lightScheme: DynamicScheme

    /**
     * Schema for light theme
     */
    val darkScheme: DynamicScheme

    companion object {
        fun fromServerTheme(theme: ServerTheme): MaterialSchemes = MaterialSchemesImpl(theme)

        fun fromColor(
            @ColorInt color: Int
        ): MaterialSchemes = MaterialSchemesImpl(color)

        /**
         * Creates a [MaterialSchemes] where the primary color is preserved as-is. This is useful for views where
         * we want it for branding purposes, especially as a background color. The resulting dark and light
         * schemes will be the same, as they are derived from the primary color and not the device theme.
         */
        fun withPrimaryAsBackground(
            @ColorInt primary: Int
        ): MaterialSchemes = MaterialSchemesImpl(primary, true)
    }
}
