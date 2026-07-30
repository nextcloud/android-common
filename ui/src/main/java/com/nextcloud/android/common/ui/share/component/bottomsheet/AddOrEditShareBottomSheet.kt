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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.nextcloud.android.common.ui.share.model.ui.label
import com.nextcloud.android.common.ui.share.repository.MockShareRepository
import kotlinx.coroutines.launch

private const val CUSTOM_SELECTION = "custom"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrEditShareBottomSheet(
    share: Share,
    internalLink: String,
    viewModel: ShareViewModel,
    permissionPresets: List<PermissionPreset> = emptyList(),
    entry: ShareEditorEntry = ShareEditorEntry.EDIT,
    onDismissDraft: (Share) -> Unit = {}
) {
    var dismissAllowed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { value -> dismissAllowed || value != SheetValue.Hidden }
    )
    val categories = remember { ShareCategory.entries.toList() }
    var selectedCategory by rememberSaveable(share.id) {
        mutableStateOf(if (share.belongsAnyoneTab) ShareCategory.Anyone else ShareCategory.Invited)
    }
    var showAdvancedSettings by rememberSaveable(share.id) { mutableStateOf(entry == ShareEditorEntry.SEND_EMAIL) }
    val context = LocalContext.current
    val propertyErrors by viewModel.propertyErrors.collectAsStateWithLifecycle()
    val pendingProperties by viewModel.pendingProperties.collectAsStateWithLifecycle()
    val hasPropertyErrors = propertyErrors.isNotEmpty()
    val sendEnabled = !hasPropertyErrors && pendingProperties.isEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ModalBottomSheet(
            onDismissRequest = {},
            sheetState = sheetState,
            dragHandle = null,
            properties = ModalBottomSheetProperties(shouldDismissOnBackPress = false),
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 4.dp, top = 12.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = share.title(context),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = {
                        dismissAllowed = true
                        scope.launch {
                            sheetState.hide()
                            if (share.shareState == ShareState.DRAFT) onDismissDraft(share) else viewModel.setActiveShare(
                                null
                            )
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "close bottom sheet",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

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
                    entry = entry,
                    permissionPresets = permissionPresets,
                    viewModel = viewModel
                )

                BasicSettingsSection(
                    share = share,
                    viewModel = viewModel
                )

                AdvancedSettingsSection(
                    share = share,
                    isExpanded = showAdvancedSettings,
                    onToggle = { showAdvancedSettings = !showAdvancedSettings },
                    viewModel = viewModel
                )

                if (share.canSend) {
                    ActionButtons(
                        share = share,
                        internalLink = internalLink,
                        category = selectedCategory,
                        sendEnabled = sendEnabled,
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

    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
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
    entry: ShareEditorEntry,
    permissionPresets: List<PermissionPreset>,
    viewModel: ShareViewModel
) {
    var userSelection by rememberSaveable(share.id) {
        mutableStateOf(
            if (entry == ShareEditorEntry.CUSTOMIZE_PERMISSION) CUSTOM_SELECTION else null
        )
    }

    val selectedPreset = when (userSelection) {
        null -> share.permissionPreset
        CUSTOM_SELECTION -> null
        else -> userSelection
    }

    PermissionPresetDropdown(
        options = PermissionPresetOption.optionsFor(share, permissionPresets),
        selectedOption = PermissionPresetOption.from(selectedPreset, permissionPresets),
        onOptionSelected = { option ->
            userSelection = option.presetClass ?: CUSTOM_SELECTION
            option.presetClass?.let { viewModel.updatePermissionPreset(share.id, it, true) }
        }
    )

    if (selectedPreset != null) return

    share.permissions.forEach { permission ->
        key(permission.clazz) {
            var checked by remember(permission.clazz, permission.enabled) {
                mutableStateOf(permission.enabled)
            }
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
    options: List<PermissionPresetOption>,
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
            value = selectedOption.label(),
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
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label()) },
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
private fun BasicSettingsSection(
    share: Share,
    viewModel: ShareViewModel
) {
    if (!share.isBasicSectionAvailable) {
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        share.basicProperties.forEach { property ->
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
private fun AdvancedSettingsSection(
    share: Share,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    viewModel: ShareViewModel
) {
    if (!share.isAdvancedSectionAvailable) {
        return
    }

    CollapsibleShareSection(
        label = stringResource(R.string.share_view_advanced_settings),
        isExpanded = isExpanded,
        onToggle = onToggle
    ) {
        share.advancedProperties.forEach { property ->
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
    val localClipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val isPreparingLink by viewModel.isPreparingLink.collectAsStateWithLifecycle()
    val shareReadyToCopy by viewModel.shareReadyToCopy.collectAsStateWithLifecycle()

    LaunchedEffect(shareReadyToCopy) {
        val ready = shareReadyToCopy ?: return@LaunchedEffect
        ready.getClipEntry(internalLink, category)?.let { localClipboard.setClipEntry(it) }
        viewModel.onLinkCopied()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Button(
            onClick = {
                if (category == ShareCategory.Anyone) {
                    viewModel.prepareLinkForCopy(share.id)
                } else {
                    scope.launch {
                        share.getClipEntry(internalLink, category)?.let { localClipboard.setClipEntry(it) }
                    }
                }
            },
            enabled = !isPreparingLink && (category != ShareCategory.Anyone || sendEnabled),
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            if (isPreparingLink) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_link),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
            }
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
                enabled = sendEnabled,
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
            viewModel = ShareViewModel(MockShareRepository(), "preview-source", SavedStateHandle())
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
        PropertyString(
            clazz = "note",
            displayName = "Note",
            priority = 10,
            required = false,
            advanced = false,
            value = ""
        )
    ),
    permissions = listOf(
        Permission(
            clazz = "read",
            displayName = "Read",
            priority = 10,
            presets = listOf("view", "edit"),
            enabled = true
        ),
        Permission(
            clazz = "update",
            displayName = "Update",
            priority = 20,
            presets = listOf("edit"),
            enabled = false
        )
    ),
    permissionPreset = null
)

