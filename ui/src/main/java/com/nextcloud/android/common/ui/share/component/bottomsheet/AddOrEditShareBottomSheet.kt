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
import androidx.compose.ui.unit.dp
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.share.ShareViewModel
import com.nextcloud.android.common.ui.share.component.CollapsibleShareSection
import com.nextcloud.android.common.ui.share.component.RecipientSearchField
import com.nextcloud.android.common.ui.share.component.ShareSwitch
import com.nextcloud.android.common.ui.share.component.property.SharePropertyView
import com.nextcloud.android.common.ui.share.model.api.share.Share
import com.nextcloud.android.common.ui.share.model.api.state.ShareState
import com.nextcloud.android.common.ui.share.model.ui.PermissionPresetOption
import com.nextcloud.android.common.ui.share.model.ui.ShareCategory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrEditShareBottomSheet(
    share: Share,
    viewModel: ShareViewModel,
    onDismissDraft: (Share) -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()
    val categories = remember { ShareCategory.entries.toList() }
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var showAdvancedSettings by remember { mutableStateOf(false) }

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
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ShareTitle(share)

            CategorySelector(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    selectedCategory = category
                    viewModel.addAnyoneRecipient(category, share)
                }
            )

            if (selectedCategory == ShareCategory.Invited) {
                RecipientSearchField(share, viewModel)
            }

            PermissionCategories(
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

            if (share.readyToSend()) {
                Buttons(share, selectedCategory, onSend = {
                    viewModel.updateState(share.id, ShareState.ACTIVE)
                })
            }
        }
    }
}

@Composable
private fun Buttons(
    share: Share,
    category: ShareCategory,
    onSend: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val clipEntry = share.getClipEntry()
        if (category == ShareCategory.Anyone && clipEntry != null) {
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

@Composable
private fun ShareTitle(share: Share) {
    val context = LocalContext.current
    Text(
        text = share.title(context),
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun CategorySelector(
    categories: List<ShareCategory>,
    selectedCategory: ShareCategory,
    onCategorySelected: (ShareCategory) -> Unit
) {
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        categories.forEachIndexed { index, category ->
            SegmentedButton(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = categories.size)
            ) {
                Text(category.name)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PermissionCategories(
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
            ShareSwitch(
                label = permission.displayName,
                checked = permission.enabled,
                onCheckedChange = { isChecked ->
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
