/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2022-2023 Nextcloud GmbH and Nextcloud contributors
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
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
