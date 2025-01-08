/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2022-2023 Nextcloud GmbH and Nextcloud contributors
 * SPDX-FileCopyrightText: 2022-2023 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-License-Identifier: MIT
 */
package com.nextcloud.android.common.ui.theme

import android.content.Context
import android.view.View
import androidx.compose.material3.ColorScheme
import com.nextcloud.android.common.ui.util.PlatformThemeUtil
import com.nextcloud.android.common.ui.util.extensions.toColorScheme
import dynamiccolor.DynamicScheme

open class ViewThemeUtilsBase(
    private val schemes: MaterialSchemes
) {
    /**
     * Scheme for painting elements
     */
    @Suppress("DeprecatedCallableAddReplaceWith")
    fun getScheme(context: Context): DynamicScheme = getSchemeInternal(context)

    fun getColorScheme(context: Context): ColorScheme = getScheme(context).toColorScheme()

    @Suppress("MemberVisibilityCanBePrivate")
    // TODO cache by context hashcode
    protected fun getSchemeInternal(context: Context): DynamicScheme =
        when {
            PlatformThemeUtil.isDarkMode(context) -> schemes.darkScheme
            else -> schemes.lightScheme
        }

    protected fun <R> withScheme(
        view: View,
        block: (DynamicScheme) -> R
    ): R = block(getSchemeInternal(view.context))

    protected fun <R> withScheme(
        context: Context,
        block: (DynamicScheme) -> R
    ): R = block(getSchemeInternal(context))

    protected fun <R> withSchemeDark(block: (DynamicScheme) -> R): R = block(schemes.darkScheme)
}
