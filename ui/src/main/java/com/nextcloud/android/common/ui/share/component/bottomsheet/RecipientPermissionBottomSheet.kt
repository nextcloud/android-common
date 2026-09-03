/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

@file:Suppress("FunctionNaming")

package com.nextcloud.android.common.ui.share.component.bottomsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.share.component.ShareSwitch
import com.nextcloud.android.common.ui.share.model.api.permission.PermissionPreset
import com.nextcloud.android.common.ui.share.model.api.recipients.Recipient
import com.nextcloud.android.common.ui.share.model.api.share.Share
import com.nextcloud.android.common.ui.share.model.ui.PermissionPresetOption
import com.nextcloud.android.common.ui.share.model.ui.effectivePermissionsFor
import com.nextcloud.android.common.ui.share.model.ui.grantedPermissions
import com.nextcloud.android.common.ui.share.model.ui.label
import com.nextcloud.android.common.ui.share.model.ui.presetOption
import com.nextcloud.android.common.ui.share.model.ui.recipientPresetOptions
import com.nextcloud.android.common.ui.share.viewmodel.ShareViewModel

private val SHEET_BOTTOM_PADDING = 32.dp
private val CONTENT_PADDING = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipientPermissionBottomSheet(
    share: Share,
    recipient: Recipient,
    permissionPresets: List<PermissionPreset>,
    viewModel: ShareViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val selectedOption = share.effectivePermissionsFor(recipient).presetOption(permissionPresets)
    var showCustomPermissions by remember(recipient.value) {
        mutableStateOf(selectedOption == PermissionPresetOption.Custom)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = SHEET_BOTTOM_PADDING)
        ) {
            Text(
                text = stringResource(R.string.share_view_recipient_permission_title, recipient.displayName),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(CONTENT_PADDING)
            )

            share.recipientPresetOptions(permissionPresets).forEach { option ->
                PresetOptionItem(
                    option = option,
                    isSelected = option == if (showCustomPermissions) PermissionPresetOption.Custom else selectedOption,
                    onClick = {
                        val presetClass = option.presetClass
                        if (presetClass == null) {
                            showCustomPermissions = true
                            return@PresetOptionItem
                        }

                        onDismiss()
                        viewModel.updateRecipientPermissionPreset(share.id, recipient, presetClass)
                    }
                )
            }

            if (!showCustomPermissions) return@Column

            HorizontalDivider()

            CustomPermissions(
                share = share,
                recipient = recipient,
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun CustomPermissions(share: Share, recipient: Recipient, viewModel: ShareViewModel) {
    val effectivePermissions = share.effectivePermissionsFor(recipient)

    Column(modifier = Modifier.padding(horizontal = CONTENT_PADDING)) {
        Text(
            text = stringResource(R.string.share_view_recipient_permission_custom_title),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = CONTENT_PADDING)
        )

        share.grantedPermissions().forEach { permission ->
            key(permission.clazz) {
                val isEnabled = effectivePermissions.first { it.clazz == permission.clazz }.enabled
                var checked by remember(permission.clazz, isEnabled) { mutableStateOf(isEnabled) }

                ShareSwitch(
                    label = permission.displayName,
                    checked = checked,
                    onCheckedChange = { value ->
                        checked = value
                        viewModel.updateRecipientPermission(share.id, recipient, permission.clazz, value)
                    }
                )
            }
        }
    }
}

@Composable
private fun PresetOptionItem(option: PermissionPresetOption, isSelected: Boolean, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        headlineContent = { Text(option.label()) },
        trailingContent = {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}
