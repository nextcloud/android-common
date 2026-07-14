/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.ui

import com.nextcloud.android.common.ui.share.model.api.permission.PermissionPreset
import com.nextcloud.android.common.ui.share.model.api.share.Share

sealed interface PermissionPresetOption {

    val presetClass: String?

    data object Custom : PermissionPresetOption {
        override val presetClass: String? = null
    }

    data class Preset(val preset: PermissionPreset) : PermissionPresetOption {
        override val presetClass: String get() = preset.clazz
    }

    companion object {
        fun optionsFor(share: Share, presets: List<PermissionPreset>): List<PermissionPresetOption> {
            val applicableClasses = share.permissions.flatMap { it.presets }.toSet()
            return presets.filter { it.clazz in applicableClasses }.map { Preset(it) } + Custom
        }

        fun from(presetClass: String?, presets: List<PermissionPreset>): PermissionPresetOption {
            if (presetClass == null) return Custom
            return presets.firstOrNull { it.clazz == presetClass }?.let { Preset(it) } ?: Custom
        }
    }
}
