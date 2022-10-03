/*
 * Nextcloud Android client application
 *
 *  @author Álvaro Brey
 *  Copyright (C) 2022 Álvaro Brey
 *  Copyright (C) 2022 Nextcloud GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU AFFERO GENERAL PUBLIC LICENSE
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU AFFERO GENERAL PUBLIC LICENSE for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.nextcloud.android.common.ui.theme.utils

import scheme.Scheme

/**
 * Parameter for library methods so that clients can choose color roles without accessing the Scheme directly
 */
enum class ColorRole(internal val select: (Scheme) -> Int) {
    ON_PRIMARY({ it.onPrimary }),
    ON_SECONDARY_CONTAINER({ it.onSecondaryContainer }),
    ON_SURFACE({ it.onSurface }),
    PRIMARY({ it.primary }),
    SURFACE({ it.surface })
}
