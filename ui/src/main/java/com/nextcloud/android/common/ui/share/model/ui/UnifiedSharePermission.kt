/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.ui

import com.nextcloud.android.common.ui.R

sealed class UnifiedSharePermission {
    // file drop only for folder
    data object FileDrop : UnifiedSharePermission()

    data object CanView : UnifiedSharePermission()
    data object CanEdit : UnifiedSharePermission()

    // create only for folder
    data class Custom(var read: Boolean, var edit: Boolean, var delete: Boolean, var create: Boolean) :
        UnifiedSharePermission() {
            companion object {
                fun getFromPermission(permission: UnifiedSharePermission?): Custom {
                    return Custom(
                        permission?.customPermissionRead() == true,
                        permission?.customPermissionEdit() == true,
                        permission?.customPermissionDelete() == true,
                        permission?.customPermissionCreate() == true
                    )
                }
            }
        }

    fun getTextId(): Int {
        return when(this) {
            FileDrop -> R.string.share_permission_file_drop
            CanView -> R.string.share_permission_can_view
            CanEdit -> R.string.share_permission_can_edit
            is Custom -> R.string.share_permission_custom
        }
    }

    fun customFlag(selector: Custom.() -> Boolean): Boolean =
        (this as? Custom)?.selector() ?: false
}

fun UnifiedSharePermission?.customPermissionRead(): Boolean = this?.customFlag { read } ?: false
fun UnifiedSharePermission?.customPermissionEdit(): Boolean = this?.customFlag { edit } ?: false
fun UnifiedSharePermission?.customPermissionDelete(): Boolean = this?.customFlag { delete } ?: false
fun UnifiedSharePermission?.customPermissionCreate(): Boolean = this?.customFlag { create } ?: false

data class CustomPermissionField(
    val labelRes: Int,
    val getValue: (UnifiedSharePermission.Custom) -> Boolean,
    val setValue: (UnifiedSharePermission.Custom, Boolean) -> UnifiedSharePermission.Custom
)

val customPermissionFields = listOf(
    CustomPermissionField(
        labelRes = R.string.share_view_view_files_switch,
        getValue = { it.read },
        setValue = { p, v -> p.copy(read = v) }
    ),
    CustomPermissionField(
        labelRes = R.string.share_view_edit_files_switch,
        getValue = { it.edit },
        setValue = { p, v -> p.copy(edit = v) }
    ),
    CustomPermissionField(
        labelRes = R.string.share_view_create_files_switch,
        getValue = { it.create },
        setValue = { p, v -> p.copy(create = v) }
    ),
    CustomPermissionField(
        labelRes = R.string.share_view_delete_files_switch,
        getValue = { it.delete },
        setValue = { p, v -> p.copy(delete = v) }
    ),
)
