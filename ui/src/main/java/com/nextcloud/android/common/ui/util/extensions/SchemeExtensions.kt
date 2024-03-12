/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2024 Nextcloud GmbH and Nextcloud contributors
 * SPDX-FileCopyrightText: 2024 Alper Ozturk <alper.ozturk@nextcloud.com>
 * SPDX-FileCopyrightText: 2024 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-License-Identifier: MIT
 */
package com.nextcloud.android.common.ui.util.extensions

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import dynamiccolor.MaterialDynamicColors
import scheme.DynamicScheme

fun DynamicScheme.toColorScheme(): ColorScheme {
    return ColorScheme(
        primary = Color(MaterialDynamicColors().primary().getArgb(this)),
        onPrimary = Color(MaterialDynamicColors().onPrimary().getArgb(this)),
        primaryContainer = Color(MaterialDynamicColors().primaryContainer().getArgb(this)),
        onPrimaryContainer = Color(MaterialDynamicColors().onPrimaryContainer().getArgb(this)),
        inversePrimary = Color(MaterialDynamicColors().inversePrimary().getArgb(this)),
        secondary = Color(MaterialDynamicColors().secondary().getArgb(this)),
        onSecondary = Color(MaterialDynamicColors().onSecondary().getArgb(this)),
        secondaryContainer = Color(MaterialDynamicColors().secondaryContainer().getArgb(this)),
        onSecondaryContainer = Color(MaterialDynamicColors().onSecondaryContainer().getArgb(this)),
        tertiary = Color(MaterialDynamicColors().tertiary().getArgb(this)),
        onTertiary = Color(MaterialDynamicColors().onTertiary().getArgb(this)),
        tertiaryContainer = Color(MaterialDynamicColors().tertiaryContainer().getArgb(this)),
        onTertiaryContainer = Color(MaterialDynamicColors().onTertiaryContainer().getArgb(this)),
        background = Color(MaterialDynamicColors().background().getArgb(this)),
        onBackground = Color(MaterialDynamicColors().onBackground().getArgb(this)),
        surface = Color(MaterialDynamicColors().surface().getArgb(this)),
        onSurface = Color(MaterialDynamicColors().onSurface().getArgb(this)),
        surfaceVariant = Color(MaterialDynamicColors().surfaceVariant().getArgb(this)),
        onSurfaceVariant = Color(MaterialDynamicColors().onSurfaceVariant().getArgb(this)),
        surfaceTint = Color(MaterialDynamicColors().surfaceVariant().getArgb(this)),
        inverseSurface = Color(MaterialDynamicColors().inverseSurface().getArgb(this)),
        inverseOnSurface = Color(MaterialDynamicColors().inverseOnSurface().getArgb(this)),
        error = Color(MaterialDynamicColors().error().getArgb(this)),
        onError = Color(MaterialDynamicColors().onError().getArgb(this)),
        errorContainer = Color(MaterialDynamicColors().errorContainer().getArgb(this)),
        onErrorContainer = Color(MaterialDynamicColors().onErrorContainer().getArgb(this)),
        outline = Color(MaterialDynamicColors().outline().getArgb(this)),
        outlineVariant = Color(MaterialDynamicColors().outlineVariant().getArgb(this)),
        scrim = Color(MaterialDynamicColors().scrim().getArgb(this))
    )
}
