/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.ui

import com.nextcloud.android.common.ui.share.model.api.permission.Permission
import com.nextcloud.android.common.ui.share.model.api.permission.PermissionPreset
import com.nextcloud.android.common.ui.share.model.api.recipients.Recipient
import com.nextcloud.android.common.ui.share.model.api.share.Share

fun Share.effectivePermissionsFor(recipient: Recipient): List<Permission> {
    val overrides = recipient.permissions.associateBy { it.clazz }
    return permissions.map { permission ->
        val override = overrides[permission.clazz] ?: return@map permission
        permission.copy(enabled = permission.enabled && override.enabled)
    }
}

fun Share.grantedPermissions(): List<Permission> = permissions.filter { it.enabled }

fun List<Permission>.presetOption(presets: List<PermissionPreset>): PermissionPresetOption {
    val enabled = filter { it.enabled }.map { it.clazz }.toSet()
    val preset =
        presets.firstOrNull { preset ->
            val members = filter { preset.clazz in it.presets }.map { it.clazz }
            members.isNotEmpty() && members.size == enabled.size && enabled.containsAll(members)
        }
    return preset?.let { PermissionPresetOption.Preset(it) } ?: PermissionPresetOption.Custom
}

fun Share.recipientPresetOptions(presets: List<PermissionPreset>): List<PermissionPresetOption> {
    val selectable =
        presets.filter { preset ->
            val members = permissions.filter { preset.clazz in it.presets }
            members.isNotEmpty() && members.all { it.enabled }
        }
    return selectable.map { PermissionPresetOption.Preset(it) } + PermissionPresetOption.Custom
}

fun Share.recipientPermissionChanges(recipient: Recipient, presetClass: String): Map<String, Boolean> {
    val effective = effectivePermissionsFor(recipient).associate { it.clazz to it.enabled }
    return grantedPermissions()
        .associate { it.clazz to (presetClass in it.presets) }
        .filterNot { (clazz, enabled) -> effective[clazz] == enabled }
}
