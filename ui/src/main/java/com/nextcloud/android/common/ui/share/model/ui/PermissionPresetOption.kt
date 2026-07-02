/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.ui

import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.share.model.api.permission.PermissionPreset

enum class PermissionPresetOption(
    val preset: PermissionPreset?,
    val labelRes: Int
) {
    CUSTOM(null, R.string.share_view_permission_preset_custom),
    VIEW(PermissionPreset.VIEW, R.string.share_view_permission_preset_view),
    EDIT(PermissionPreset.EDIT, R.string.share_view_permission_preset_edit);

    companion object {
        fun from(preset: PermissionPreset?): PermissionPresetOption =
            entries.firstOrNull { it.preset == preset } ?: CUSTOM
    }
}
