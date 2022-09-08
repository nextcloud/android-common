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
import scheme.Scheme

internal class MaterialSchemesImpl :
    MaterialSchemes {
    override val lightScheme: Scheme
    override val darkScheme: Scheme

    constructor(@ColorInt primaryColor: Int) {
        lightScheme = Scheme.light(primaryColor)
        darkScheme = Scheme.dark(primaryColor)
    }

    constructor(serverTheme: ServerTheme) : this(serverTheme.primaryColor)
}
