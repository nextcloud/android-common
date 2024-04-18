/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2022-2023 Nextcloud GmbH and Nextcloud contributors
 * SPDX-FileCopyrightText: 2022-2023 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-License-Identifier: MIT
 */
package com.nextcloud.android.common.ui.theme

import androidx.annotation.ColorInt
import dynamiccolor.DynamicScheme

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
