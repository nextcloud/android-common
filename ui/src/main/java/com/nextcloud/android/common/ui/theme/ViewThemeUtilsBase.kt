/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2022-2023 Nextcloud GmbH and Nextcloud contributors
 * SPDX-FileCopyrightText: 2022-2023 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
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
