/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2022-2023 Nextcloud GmbH and Nextcloud contributors
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-License-Identifier: MIT
 */
package com.nextcloud.android.common.ui.theme

import androidx.annotation.ColorInt

interface ServerTheme {
    @get:ColorInt
    val primaryColor: Int

    /**
     * Default element color
     */
    @get:ColorInt
    val colorElement: Int

    /**
     * Element color for bright backgrounds
     */
    @get:ColorInt
    val colorElementBright: Int

    /**
     * Element color for dark backgrounds
     */
    @get:ColorInt
    val colorElementDark: Int

    /**
     * Text color for elements
     */
    @get:ColorInt
    val colorText: Int
}
