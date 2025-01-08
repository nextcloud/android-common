/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2022-2023 Nextcloud GmbH and Nextcloud contributors
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-License-Identifier: MIT
 */
package com.nextcloud.android.common.ui.theme

import androidx.annotation.ColorInt
import dynamiccolor.DynamicScheme
import hct.Hct
import scheme.SchemeContent
import scheme.SchemeTonalSpot

internal class MaterialSchemesImpl : MaterialSchemes {
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
