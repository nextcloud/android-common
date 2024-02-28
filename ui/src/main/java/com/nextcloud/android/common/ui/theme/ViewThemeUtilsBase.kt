/*
 * Nextcloud Android Common Library
 *
 * @author Álvaro Brey
 * @author Andy Scherzinger
 * Copyright (C) 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * Copyright (C) 2022-2023 Andy Scherzinger <info@andy-scherzinger.de>
 * Copyright (C) 2022-2023 Nextcloud GmbH
 *
 * SPDX-FileCopyrightText: 2022-2023 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
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

import android.content.Context
import android.view.View
import com.nextcloud.android.common.ui.util.PlatformThemeUtil
import scheme.Scheme

open class ViewThemeUtilsBase(private val schemes: MaterialSchemes) {
    /**
     * Scheme for painting elements
     */
    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(
        "Implement view-specific theming functions in a `ViewThemeUtilsBase` subclass" +
            " instead of getting the scheme directly"
    )
    fun getScheme(context: Context): Scheme = getSchemeInternal(context)

    @Suppress("MemberVisibilityCanBePrivate")
    // TODO cache by context hashcode
    protected fun getSchemeInternal(context: Context): Scheme =
        when {
            PlatformThemeUtil.isDarkMode(context) -> schemes.darkScheme
            else -> schemes.lightScheme
        }

    protected fun <R> withScheme(
        view: View,
        block: (Scheme) -> R
    ): R = block(getSchemeInternal(view.context))

    protected fun <R> withScheme(
        context: Context,
        block: (Scheme) -> R
    ): R = block(getSchemeInternal(context))

    protected fun <R> withSchemeDark(block: (Scheme) -> R): R = block(schemes.darkScheme)
}
