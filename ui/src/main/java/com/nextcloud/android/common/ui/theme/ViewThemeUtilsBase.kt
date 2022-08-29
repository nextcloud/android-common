/*
 * Nextcloud Android Common Library
 *
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

import android.content.Context
import android.view.View
import com.nextcloud.android.common.ui.util.PlatformThemeUtil
import scheme.Scheme

abstract class ViewThemeUtilsBase(val schemes: MaterialSchemes) {
    /**
     * Scheme for painting elements
     */
    fun getScheme(context: Context): Scheme = when {
        PlatformThemeUtil.isDarkMode(context) -> schemes.darkScheme
        else -> schemes.lightScheme
    }

    protected fun withScheme(view: View, block: (Scheme) -> Unit) {
        block(getScheme(view.context))
    }

    protected fun withScheme(context: Context, block: (Scheme) -> Unit) {
        block(getScheme(context))
    }

    protected fun withSchemeDark(block: (Scheme) -> Unit) {
        block(schemes.darkScheme)
    }
}
