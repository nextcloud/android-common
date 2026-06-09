/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */


package com.nextcloud.android.common.ui.share.component.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.share.ShareViewModel
import com.nextcloud.android.common.ui.share.component.CollapsibleShareSection
import com.nextcloud.android.common.ui.share.component.RecipientSearchField
import com.nextcloud.android.common.ui.share.component.ShareSwitch
import com.nextcloud.android.common.ui.share.component.property.SharePropertyView
import com.nextcloud.android.common.ui.share.model.api.capabilities.SharingCapabilities
import com.nextcloud.android.common.ui.share.model.api.property.clazz
import com.nextcloud.android.common.ui.share.model.api.property.priority
import com.nextcloud.android.common.ui.share.model.api.share.Share
import com.nextcloud.android.common.ui.share.model.api.state.ShareState
import com.nextcloud.android.common.ui.share.model.ui.ShareCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrEditShareBottomSheet(
    share: Share,
    sharingCapabilities: SharingCapabilities,
    viewModel: ShareViewModel,
    onDismissDraft: (Share) -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()
    val categories = remember { ShareCategory.entries.toList() }
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var showAdvancedSettings by remember { mutableStateOf(false) }
    var expandedCategories by remember { mutableStateOf(emptySet<String>()) }

    ModalBottomSheet(
        onDismissRequest = {
            if (share.shareState == ShareState.DRAFT) {
                onDismissDraft(share)
            } else {
                viewModel.commitPendingProperties(share.id)
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
                onCategorySelected = { selectedCategory = it }
            )

            if (selectedCategory == ShareCategory.Invited) {
                RecipientSearchField(share, viewModel)
            }

            PermissionCategories(
                share = share,
                sharingCapabilities = sharingCapabilities,
                expandedCategories = expandedCategories,
                onToggleCategory = { categoryName ->
                    expandedCategories = if (expandedCategories.contains(categoryName)) {
                        expandedCategories - categoryName
                    } else {
                        expandedCategories + categoryName
                    }
                },
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

// TODO ADD send button
// TODO disable multi receipients
// TODO implement Anyone -->  /ocs/v2.php/apps/sharing/api/v1/share/{id}/recipient
//  --> Add a recipient to a share use correspongind class value --> doesnt matter use uuid string : "value": "string",   "instance": "string"

// TODO discuss with laura for note to recepipent expiraitaion date proeprties etc should be collabls

// TODO send ISO 8601 as string to backend for expiration date

// TODO for COPY link use Recepient.Secret.URL side note: if value is updatable user can edit the TOKEN directly which is value

// TODO display error messages and handle them via debounce mechanism

// TODO fetch shares currently returns all shares SOURCE ID will be implemented so that u can only show related shares

// TODO show share icon from first recipient but check with laura

// TODO: Backend will change and provide us bundled permissions or list of custom permissions.
// USE THIS FOR BOTH OPTION
@Composable
private fun PermissionCategories(
    share: Share,
    sharingCapabilities: SharingCapabilities,
    expandedCategories: Set<String>,
    onToggleCategory: (String) -> Unit,
    viewModel: ShareViewModel
) {
    /*
     var expanded by remember { mutableStateOf(false) }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        share.permissions.forEach {
            key(it.displayName) {
                DropdownMenuItem(
                    text = { Text(it.displayName) },
                    onClick = { /* Do something... */ }
                )
            }
        }

        DropdownMenuItem(
            text = { Text("Option 2") },
            onClick = { /* Do something... */ }
        )
    }
     */


    sharingCapabilities.permissionCategoryTypes
        .sortedBy { it.priority }
        .forEach { sharingCapability ->
            key(sharingCapability.classField) {
                CollapsibleShareSection(
                    label = sharingCapability.displayName,
                    isExpanded = sharingCapability.displayName in expandedCategories,
                    onToggle = { onToggleCategory(sharingCapability.displayName) },
                ) {
                    share.permissions
                        .filter { permission -> permission.category == sharingCapability.classField }
                        .sortedBy { it.displayName }
                        .forEach { permission ->
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
