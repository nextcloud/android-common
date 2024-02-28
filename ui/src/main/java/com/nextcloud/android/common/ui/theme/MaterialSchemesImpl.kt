/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2022-2023 Nextcloud GmbH and Nextcloud contributors
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.nextcloud.android.common.ui.theme

import androidx.annotation.ColorInt
import hct.Hct
import scheme.Scheme

internal class MaterialSchemesImpl :
    MaterialSchemes {
    override val lightScheme: Scheme
    override val darkScheme: Scheme

    constructor(
        @ColorInt primaryColor: Int,
        primaryAsBackground: Boolean = false
    ) {
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
        val scheme =
            if (isDark) {
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
