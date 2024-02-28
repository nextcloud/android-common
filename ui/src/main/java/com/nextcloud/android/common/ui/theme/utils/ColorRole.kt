/*
 * Nextcloud Android client application
 *
 *  @author Álvaro Brey
 *  Copyright (C) 2022 Álvaro Brey
 *  Copyright (C) 2022-2023 Nextcloud GmbH
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
 *
 */

package com.nextcloud.android.common.ui.theme.utils

import scheme.Scheme

/**
 * Parameter for library methods so that clients can choose color roles without accessing the Scheme directly
 */
enum class ColorRole(internal val select: (Scheme) -> Int) {
    PRIMARY({ it.primary }),
    ON_PRIMARY({ it.onPrimary }),
    PRIMARY_CONTAINER({ it.primaryContainer }),
    ON_PRIMARY_CONTAINER({ it.onPrimaryContainer }),
    SECONDARY({ it.secondary }),
    ON_SECONDARY({ it.onSecondary }),
    SECONDARY_CONTAINER({ it.secondaryContainer }),
    ON_SECONDARY_CONTAINER({ it.onSecondaryContainer }),
    ERROR({ it.error }),
    ON_ERROR({ it.onError }),
    ERROR_CONTAINER({ it.errorContainer }),
    ON_ERROR_CONTAINER({ it.onErrorContainer }),
    BACKGROUND({ it.background }),
    ON_BACKGROUND({ it.onBackground }),
    SURFACE({ it.surface }),
    ON_SURFACE({ it.onSurface }),
    SURFACE_VARIANT({ it.surfaceVariant }),
    ON_SURFACE_VARIANT({ it.onSurfaceVariant }),
    OUTLINE({ it.outline }),
    OUTLINE_VARIANT({ it.outlineVariant })
}
