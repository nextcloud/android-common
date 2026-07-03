/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */


package com.nextcloud.android.common.ui.share.component.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.share.ShareViewModel
import com.nextcloud.android.common.ui.share.component.CollapsibleShareSection
import com.nextcloud.android.common.ui.share.component.RecipientTokenField
import com.nextcloud.android.common.ui.share.component.SelectRecipientField
import com.nextcloud.android.common.ui.share.component.ShareSwitch
import com.nextcloud.android.common.ui.share.component.property.SharePropertyView
import com.nextcloud.android.common.ui.share.model.api.permission.Permission
import com.nextcloud.android.common.ui.share.model.api.permission.PermissionPreset
import com.nextcloud.android.common.ui.share.model.api.property.PropertyString
import com.nextcloud.android.common.ui.share.model.api.recipients.Recipient
import com.nextcloud.android.common.ui.share.model.api.secret.Secret
import com.nextcloud.android.common.ui.share.model.api.share.Share
import com.nextcloud.android.common.ui.share.model.api.source.Source
import com.nextcloud.android.common.ui.share.model.api.state.ShareState
import com.nextcloud.android.common.ui.share.model.api.user.User
import com.nextcloud.android.common.ui.share.model.ui.PermissionPresetOption
import com.nextcloud.android.common.ui.share.model.ui.ShareCategory
import com.nextcloud.android.common.ui.share.repository.MockShareRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrEditShareBottomSheet(
    share: Share,
    viewModel: ShareViewModel,
    onDismissDraft: (Share) -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val categories = remember { ShareCategory.entries.toList() }
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var showAdvancedSettings by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val propertyErrors by viewModel.propertyErrors.collectAsState()
    val hasPropertyErrors = propertyErrors.values.any { it != null }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ModalBottomSheet(
            onDismissRequest = {
                if (share.shareState == ShareState.DRAFT) {
                    onDismissDraft(share)
                } else {
                    viewModel.setActiveShare(null)
                }
            },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = share.title(context),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp)
                )

                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    categories.forEachIndexed { index, category ->
                        SegmentedButton(
                            selected = selectedCategory == category,
                            onClick = {
                                selectedCategory = category
                                viewModel.addAnyoneRecipient(category, share)
                            },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = categories.size),
                            icon = {
                                Icon(painter = painterResource(category.iconId), contentDescription = "")
                            }
                        ) {
                            Text(stringResource(category.titleId))
                        }
                    }
                }

                if (selectedCategory == ShareCategory.Invited) {
                    SelectRecipientField(share, viewModel)
                }

                // TODO: is it going to be included for now?
                val editableRecipient = share.recipients.firstOrNull { it.secret.updatable }
                if (editableRecipient != null) {
                    RecipientTokenField(
                        recipient = editableRecipient,
                        onTokenChange = { token ->
                            viewModel.updateRecipientSecret(share.id, editableRecipient, token)
                        }
                    )
                }

                PermissionsView(
                    share = share,
                    viewModel = viewModel
                )

                if (share.properties.isNotEmpty()) {
                    AdvancedSettingsSection(
                        share = share,
                        isExpanded = showAdvancedSettings,
                        onToggle = { showAdvancedSettings = !showAdvancedSettings },
                        viewModel = viewModel
                    )
                }

                if (hasPropertyErrors) {
                    Text(
                        text = stringResource(R.string.share_view_property_error_warning),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }

                if (share.readyToSend()) {
                    ActionButtons(
                        share = share,
                        sendEnabled = !hasPropertyErrors,
                        onSend = { viewModel.updateState(share.id, ShareState.ACTIVE) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PermissionsView(
    share: Share,
    viewModel: ShareViewModel
) {
    var selectedPreset by remember(share.id) { mutableStateOf(share.permissionPreset) }

    PermissionPresetDropdown(
        selectedOption = PermissionPresetOption.from(selectedPreset),
        onOptionSelected = { option ->
            selectedPreset = option.preset
            option.preset?.let { viewModel.updatePermissionPreset(share.id, it) }
        }
    )

    if (selectedPreset != null) return

    share.permissions.forEach { permission ->
        key(permission.clazz) {
            var checked by remember { mutableStateOf(permission.enabled) }
            ShareSwitch(
                label = permission.displayName,
                checked = checked,
                onCheckedChange = { isChecked ->
                    checked = isChecked
                    viewModel.updatePermission(share.id, permission.clazz, isChecked)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PermissionPresetDropdown(
    selectedOption: PermissionPresetOption,
    onOptionSelected: (PermissionPresetOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = Modifier.padding(12.dp),
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = stringResource(selectedOption.labelRes),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.share_view_permission_preset_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            PermissionPresetOption.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(stringResource(option.labelRes)) },
                    onClick = {
                        expanded = false
                        onOptionSelected(option)
                    }
                )
            }
        }
    }
}

@Composable
private fun AdvancedSettingsSection(
    share: Share,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    viewModel: ShareViewModel
) {
    CollapsibleShareSection(
        label = stringResource(R.string.share_view_advanced_settings),
        isExpanded = isExpanded,
        onToggle = onToggle
    ) {
        share.properties.sortedBy { it.priority }.forEach { property ->
            key(property.clazz) {
                SharePropertyView(
                    shareId = share.id,
                    property = property,
                    viewModel = viewModel
                )
            }
        }
    }
}


@Composable
private fun ActionButtons(
    share: Share,
    sendEnabled: Boolean,
    onSend: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val clipEntry = share.getClipEntry()
        if (clipEntry != null) {
            val localClipboard = LocalClipboard.current
            val scope = rememberCoroutineScope()

            Button(
                onClick = {
                    scope.launch {
                        localClipboard.setClipEntry(clipEntry)
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_link),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = stringResource(R.string.share_view_copy_action),
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }

        Button(
            onClick = onSend,
            enabled = sendEnabled,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = stringResource(R.string.share_view_send_action),
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddOrEditShareBottomSheetPreview() {
    MaterialTheme {
        AddOrEditShareBottomSheet(
            share = previewShare,
            viewModel = ShareViewModel(MockShareRepository())
        )
    }
}

private val previewIcon =
    com.nextcloud.android.common.ui.share.model.api.icon.Icon(svg = "<svg xmlns=\"http://www.w3.org/2000/svg\"/>")

private val previewUser = User(
    userId = "alice",
    displayName = "Alice Johnson",
    icon = previewIcon
)

private val previewShare = Share(
    id = "preview",
    owner = previewUser,
    lastUpdated = 0L,
    shareState = ShareState.DRAFT,
    sources = listOf(
        Source(clazz = "file", value = "/Photos/vacation.jpg", displayName = "vacation.jpg", icon = previewIcon)
    ),
    recipients = listOf(
        Recipient(
            clazz = "user",
            value = "bob@example.com",
            displayName = "Bob Smith",
            icon = previewIcon,
            secret = Secret(updatable = false, value = "", url = "https://example.com/s/abc123")
        )
    ),
    properties = listOf(
        PropertyString(clazz = "note", displayName = "Note", priority = 10, required = false, value = "")
    ),
    permissions = listOf(
        Permission(
            clazz = "read",
            displayName = "Read",
            presets = listOf(PermissionPreset.VIEW, PermissionPreset.EDIT),
            enabled = true
        ),
        Permission(
            clazz = "update",
            displayName = "Update",
            presets = listOf(PermissionPreset.EDIT),
            enabled = false
        )
    ),
    permissionPreset = null
)

