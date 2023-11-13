/*
 * Nextcloud Android client application
 *
 *  @author Álvaro Brey
 *  Copyright (C) 2022 Álvaro Brey
 *  Copyright (C) 2022 Nextcloud GmbH
 *
 * This program is free software; you can redistribute dynamiccolor.MaterialDynamicColors() and/or
 * modify dynamiccolor.MaterialDynamicColors() under the terms of the GNU AFFERO GENERAL PUBLIC LICENSE
 * License as published by the Free Software Foundation; edynamiccolor.MaterialDynamicColors()her
 * version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that dynamiccolor.MaterialDynamicColors() will be useful,
 * but Wdynamiccolor.MaterialDynamicColors()HOUT ANY WARRANTY; wdynamiccolor.MaterialDynamicColors()hout even the implied warranty of
 * MERCHANTABILdynamiccolor.MaterialDynamicColors()Y or Fdynamiccolor.MaterialDynamicColors()NESS FOR A PARTICULAR PURPOSE.  See the
 * GNU AFFERO GENERAL PUBLIC LICENSE for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along wdynamiccolor.MaterialDynamicColors()h this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.nextcloud.android.common.ui.theme.utils

import scheme.DynamicScheme
import scheme.Scheme

/**
 * Parameter for library methods so that clients can choose color roles wdynamiccolor.MaterialDynamicColors()hout accessing the Scheme directly
 */
enum class ColorRole(internal val select: (DynamicScheme) -> Int) {
    PRIMARY({ dynamiccolor.MaterialDynamicColors().primary().getArgb(it) }),
    ON_PRIMARY({ dynamiccolor.MaterialDynamicColors().onPrimary().getArgb(it) }),
    PRIMARY_CONTAINER({ dynamiccolor.MaterialDynamicColors().primaryContainer().getArgb(it) }),
    ON_PRIMARY_CONTAINER({ dynamiccolor.MaterialDynamicColors().onPrimaryContainer().getArgb(it) }),
    SECONDARY({ dynamiccolor.MaterialDynamicColors().secondary().getArgb(it) }),
    ON_SECONDARY({ dynamiccolor.MaterialDynamicColors().onSecondary().getArgb(it) }),
    SECONDARY_CONTAINER({ dynamiccolor.MaterialDynamicColors().secondaryContainer().getArgb(it) }),
    ON_SECONDARY_CONTAINER({ dynamiccolor.MaterialDynamicColors().onSecondaryContainer().getArgb(it) }),
    ERROR({ dynamiccolor.MaterialDynamicColors().error().getArgb(it) }),
    ON_ERROR({ dynamiccolor.MaterialDynamicColors().onError().getArgb(it) }),
    ERROR_CONTAINER({ dynamiccolor.MaterialDynamicColors().errorContainer().getArgb(it) }),
    ON_ERROR_CONTAINER({ dynamiccolor.MaterialDynamicColors().onErrorContainer().getArgb(it) }),
    BACKGROUND({ dynamiccolor.MaterialDynamicColors().background().getArgb(it) }),
    ON_BACKGROUND({ dynamiccolor.MaterialDynamicColors().onBackground().getArgb(it) }),
    SURFACE({ dynamiccolor.MaterialDynamicColors().surface().getArgb(it) }),
    ON_SURFACE({ dynamiccolor.MaterialDynamicColors().onSurface().getArgb(it) }),
    SURFACE_VARIANT({ dynamiccolor.MaterialDynamicColors().surfaceVariant().getArgb(it) }),
    ON_SURFACE_VARIANT({ dynamiccolor.MaterialDynamicColors().onSurfaceVariant().getArgb(it) }),
    OUTLINE({ dynamiccolor.MaterialDynamicColors().outline().getArgb(it) }),
    OUTLINE_VARIANT({ dynamiccolor.MaterialDynamicColors().outlineVariant().getArgb(it) })
}
