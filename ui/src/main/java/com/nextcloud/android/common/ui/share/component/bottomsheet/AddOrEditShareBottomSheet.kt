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
import com.nextcloud.android.common.ui.share.component.CustomLink
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
import com.nextcloud.android.common.ui.share.model.ui.ShareEditorEntry
import com.nextcloud.android.common.ui.share.repository.MockShareRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrEditShareBottomSheet(
    share: Share,
    internalLink: String,
    viewModel: ShareViewModel,
    entry: ShareEditorEntry = ShareEditorEntry.EDIT,
    onDismissDraft: (Share) -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val categories = remember { ShareCategory.entries.toList() }
    var selectedCategory by remember(share.id) {
        mutableStateOf(if (share.belongsAnyoneTab) ShareCategory.Anyone else ShareCategory.Invited)
    }
    val initialPresetOption = when (entry) {
        ShareEditorEntry.CUSTOMIZE_PERMISSION -> PermissionPresetOption.CUSTOM
        else -> null
    }
    var showAdvancedSettings by remember(share.id) { mutableStateOf(entry == ShareEditorEntry.SEND_EMAIL) }
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

                ShareCategorySelector(
                    share = share,
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { category ->
                        selectedCategory = category
                        viewModel.selectCategory(category, share)
                    }
                )

                if (selectedCategory == ShareCategory.Invited) {
                    SelectRecipientField(share, viewModel)
                }

                PermissionsView(
                    share = share,
                    initialPresetOption = initialPresetOption,
                    viewModel = viewModel
                )

                if (share.properties.isNotEmpty() || share.customLinkRecipient != null) {
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

                if (share.canSend) {
                    ActionButtons(
                        share = share,
                        internalLink = internalLink,
                        category = selectedCategory,
                        sendEnabled = !hasPropertyErrors,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShareCategorySelector(
    share: Share,
    categories: List<ShareCategory>,
    selectedCategory: ShareCategory,
    onCategorySelected: (ShareCategory) -> Unit
) {
    // only allow user to select between taps if it is draft share
    if (share.shareState != ShareState.DRAFT) return

    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        categories.forEachIndexed { index, category ->
            SegmentedButton(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = categories.size),
                icon = {
                    Icon(painter = painterResource(category.iconId), contentDescription = "")
                }
            ) {
                Text(stringResource(category.titleId))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PermissionsView(
    share: Share,
    initialPresetOption: PermissionPresetOption?,
    viewModel: ShareViewModel
) {
    var selectedPreset by remember(share.id) {
        mutableStateOf(if (initialPresetOption != null) initialPresetOption.preset else share.permissionPreset)
    }

    PermissionPresetDropdown(
        selectedOption = PermissionPresetOption.from(selectedPreset),
        onOptionSelected = { option ->
            selectedPreset = option.preset
            option.preset?.let { viewModel.updatePermissionPreset(share.id, it, true) }
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

        share.customLinkRecipient?.let {
            CustomLink(
                recipient = it,
                onGenerateSecret = { viewModel.generateSecret() },
                onTokenChange = { token ->
                    viewModel.updateRecipientSecret(share.id, it, token)
                }
            )
        }
    }
}

@Composable
private fun ActionButtons(
    share: Share,
    internalLink: String,
    category: ShareCategory,
    sendEnabled: Boolean,
    viewModel: ShareViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val localClipboard = LocalClipboard.current
        val scope = rememberCoroutineScope()

        Button(
            onClick = {
                scope.launch {
                    share.getClipEntry(internalLink, category)?.let {
                        if (category == ShareCategory.Anyone) {

                            // recompose ActionButtons
                            viewModel.updateState(share.id, ShareState.ACTIVE, updateAndDontDismiss = true)

                            // share object is updated active share
                            if (share.shareState == ShareState.ACTIVE) {
                                localClipboard.setClipEntry(it)
                            }
                        } else {
                            localClipboard.setClipEntry(it)
                        }
                    }
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
                text = stringResource(category.copyLinkTitleId),
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        if (category == ShareCategory.Invited) {
            Button(
                onClick = {
                    viewModel.updateState(share.id, ShareState.ACTIVE)
                },
                enabled = sendEnabled,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Icon(
                    imageVector = category.sendActionIcon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = stringResource(category.sendActionTitleId),
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        } else {
            Button(
                onClick = {
                    viewModel.updateState(share.id, ShareState.ACTIVE)
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            ) {
                Text(
                    text = stringResource(category.sendActionTitleId),
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddOrEditShareBottomSheetPreview() {
    MaterialTheme {
        AddOrEditShareBottomSheet(
            share = previewShare,
            internalLink = "internal_link",
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

