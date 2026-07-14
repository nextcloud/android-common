/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nextcloud.android.common.ui.R

@Composable
fun PermissionPresetOption.label(): String = when (this) {
    PermissionPresetOption.Custom -> stringResource(R.string.share_view_permission_preset_custom)
    is PermissionPresetOption.Preset -> preset.displayName
}
