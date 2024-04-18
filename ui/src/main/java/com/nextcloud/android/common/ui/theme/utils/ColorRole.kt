/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2022-2023 Nextcloud GmbH and Nextcloud contributors
 * SPDX-FileCopyrightText: 2023 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-License-Identifier: MIT
 */
package com.nextcloud.android.common.ui.theme.utils

import dynamiccolor.DynamicScheme

/**
 * Parameter for library methods so that clients can choose color roles without accessing the Scheme directly
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
    OUTLINE_VARIANT({ dynamiccolor.MaterialDynamicColors().outlineVariant().getArgb(it) }),
    SURFACE_CONTAINER_LOW({ dynamiccolor.MaterialDynamicColors().surfaceContainerLow().getArgb(it) })
}
