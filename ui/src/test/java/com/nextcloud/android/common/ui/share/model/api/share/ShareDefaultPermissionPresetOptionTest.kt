/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api.share

import com.nextcloud.android.common.ui.share.model.api.icon.Icon
import com.nextcloud.android.common.ui.share.model.api.permission.Permission
import com.nextcloud.android.common.ui.share.model.api.permission.PermissionPreset
import com.nextcloud.android.common.ui.share.model.api.state.ShareState
import com.nextcloud.android.common.ui.share.model.api.user.User
import com.nextcloud.android.common.ui.share.model.ui.PermissionPresetOption
import org.junit.Assert.assertEquals
import org.junit.Test

class ShareDefaultPermissionPresetOptionTest {

    @Test
    fun `returns view preset when only read is enabled`() {
        val share = shareWithEnabled(read = true, update = false, delete = false)

        val result = share.getDefaultPermissionPresetOption(PRESETS)

        assertEquals(PermissionPresetOption.Preset(VIEW_PRESET), result)
    }

    @Test
    fun `returns edit preset when all permissions are enabled`() {
        val share = shareWithEnabled(read = true, update = true, delete = true)

        val result = share.getDefaultPermissionPresetOption(PRESETS)

        assertEquals(PermissionPresetOption.Preset(EDIT_PRESET), result)
    }

    @Test
    fun `returns custom when only some permissions are enabled`() {
        val share = shareWithEnabled(read = true, update = true, delete = false)

        val result = share.getDefaultPermissionPresetOption(PRESETS)

        assertEquals(PermissionPresetOption.Custom, result)
    }

    @Test
    fun `returns custom when no permission is enabled`() {
        val share = shareWithEnabled(read = false, update = false, delete = false)

        val result = share.getDefaultPermissionPresetOption(PRESETS)

        assertEquals(PermissionPresetOption.Custom, result)
    }

    @Test
    fun `ignores enabled permissions that belong to no preset`() {
        val share = shareWith(
            permission("read", listOf("view", "edit"), enabled = true),
            permission("update", listOf("edit"), enabled = false),
            permission("delete", listOf("edit"), enabled = false),
            permission("download", emptyList(), enabled = true)
        )

        val result = share.getDefaultPermissionPresetOption(PRESETS)

        assertEquals(PermissionPresetOption.Preset(VIEW_PRESET), result)
    }

    private fun shareWithEnabled(read: Boolean, update: Boolean, delete: Boolean) = shareWith(
        permission("read", listOf("view", "edit"), read),
        permission("update", listOf("edit"), update),
        permission("delete", listOf("edit"), delete)
    )

    private fun shareWith(vararg permissions: Permission) = Share(
        id = "1",
        owner = OWNER,
        lastUpdated = 0L,
        shareState = ShareState.DRAFT,
        sources = emptyList(),
        recipients = emptyList(),
        properties = emptyList(),
        permissions = permissions.toList()
    )

    private fun permission(clazz: String, presets: List<String>, enabled: Boolean) = Permission(
        clazz = clazz,
        displayName = clazz,
        priority = 1,
        presets = presets,
        enabled = enabled
    )

    private companion object {
        private val VIEW_PRESET = PermissionPreset(clazz = "view", displayName = "Can view")
        private val EDIT_PRESET = PermissionPreset(clazz = "edit", displayName = "Can edit")
        private val PRESETS = listOf(VIEW_PRESET, EDIT_PRESET)
        private val OWNER = User(userId = "owner", displayName = "Owner", icon = Icon())
    }
}
