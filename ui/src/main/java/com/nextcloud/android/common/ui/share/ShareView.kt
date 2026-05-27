/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.component.ContentUnavailableView
import com.nextcloud.android.common.ui.network.auth.ServerCredentials
import com.nextcloud.android.common.ui.network.http.NextcloudHttpClient
import com.nextcloud.android.common.ui.share.model.api.property.Property
import com.nextcloud.android.common.ui.share.model.api.property.PropertyBoolean
import com.nextcloud.android.common.ui.share.model.api.property.PropertyDate
import com.nextcloud.android.common.ui.share.model.api.property.PropertyEnum
import com.nextcloud.android.common.ui.share.model.api.property.PropertyPassword
import com.nextcloud.android.common.ui.share.model.api.property.PropertyString
import com.nextcloud.android.common.ui.share.model.api.property.priority
import com.nextcloud.android.common.ui.share.model.api.share.Share
import com.nextcloud.android.common.ui.share.model.ui.ShareBottomSheetState
import com.nextcloud.android.common.ui.share.repository.MockShareRepository
import com.nextcloud.android.common.ui.share.repository.ShareRemoteRepository

@Composable
private fun ShareView(viewModel: ShareViewModel) {
    val errorMessageId by viewModel.errorMessageId.collectAsState()
    var bottomSheetState by remember { mutableStateOf<ShareBottomSheetState>(ShareBottomSheetState.Idle) }
    val shares by viewModel.shares.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessageId) {
        errorMessageId?.let {
            snackbarHostState.showSnackbar(context.getString(it))
            viewModel.updateErrorMessage(null)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Create an empty draft share immediately on the server, then edit it
                    viewModel.createShare { draft ->
                        bottomSheetState = ShareBottomSheetState.Edit(draft)
                    }
                },
            ) {
                Icon(painterResource(R.drawable.ic_person_add), contentDescription = "Add")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        if (shares.isEmpty()) {
            ContentUnavailableView(
                iconId = R.drawable.ic_person_add,
                title =
                    stringResource(R.string.share_view_empty_title),
                description = stringResource(R.string.share_view_empty_description)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(shares) { index, share ->
                    val type = when (index) {
                        0 -> UnifiedSharesListItemType.Top
                        shares.lastIndex -> UnifiedSharesListItemType.Bottom
                        else -> UnifiedSharesListItemType.Mid
                    }

                    if (index == 0) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    UnifiedSharesListItem(
                        share = share,
                        type = type,
                        onSelectShare = { selected -> bottomSheetState = ShareBottomSheetState.Edit(selected) },
                        onDeleteShare = { viewModel.deleteShare(it.id) },
                        onSendEmail = { /* TODO */ }
                    )
                }
            }
        }
    }

    if (bottomSheetState is ShareBottomSheetState.Edit) {
        val state = bottomSheetState as ShareBottomSheetState.Edit
        AddOrEditShareBottomSheet(
            share = state.share,
            viewModel = viewModel,
            onDismiss = { bottomSheetState = ShareBottomSheetState.Idle }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddOrEditShareBottomSheet(
    share: Share,
    viewModel: ShareViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()

    // Group permissions dynamically by Category provided by the backend
    val categories = remember(share.permissions) {
        share.permissions.mapNotNull { it.category }.distinct()
    }
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull() ?: "") }

    var showAdvancedSettings by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
            Text(
                text = stringResource(R.string.share_view_bottom_sheet_edit_title, share.id),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Dynamic Category Selector
            if (categories.size > 1) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    categories.forEachIndexed { index, category ->
                        SegmentedButton(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = categories.size)
                        ) {
                            Text(category)
                        }
                    }
                }
            }

            // Render Permissions for Selected Category
            val activePermissions = share.permissions.filter { it.category == selectedCategory }
            activePermissions.forEach { permission ->
                SettingsSwitchRow(
                    label = permission.displayName,
                    checked = permission.enabled,
                    onCheckedChange = { isChecked ->
                        viewModel.updatePermission(share.id, permission.clazz, isChecked)
                    }
                )
            }

            // Render Dynamic Properties
            if (share.properties.isNotEmpty()) {
                CollapsibleSettingsSection(
                    isExpanded = showAdvancedSettings,
                    onToggle = { showAdvancedSettings = !showAdvancedSettings }
                ) {
                    // Sort by server-defined priority
                    share.properties.sortedBy { it.priority }.forEach { property ->
                        DynamicPropertyField(share.id, property, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun DynamicPropertyField(shareId: String, property: Property, viewModel: ShareViewModel) {
    when (property) {
        is PropertyBoolean -> {
            SettingsSwitchRow(
                label = property.displayName,
                checked = property.value == "true",
                onCheckedChange = { isChecked ->
                    viewModel.updateProperty(shareId, property.clazz, isChecked.toString())
                }
            )
        }

        is PropertyString -> {
            OutlinedTextField(
                value = property.value ?: "",
                onValueChange = { viewModel.updateProperty(shareId, property.clazz, it) },
                label = { Text(property.displayName) },
                placeholder = property.hint?.let { { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                singleLine = true
            )
        }

        is PropertyPassword -> {
            OutlinedTextField(
                value = property.value ?: "",
                onValueChange = { viewModel.updateProperty(shareId, property.clazz, it) },
                label = { Text(property.displayName) },
                placeholder = property.hint?.let { { Text(it) } },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                singleLine = true
            )
        }

        is PropertyDate -> {
            // TODO: Wrap with a DatePickerDialog. Falling back to string entry for now.
            OutlinedTextField(
                value = property.value ?: "",
                onValueChange = { viewModel.updateProperty(shareId, property.clazz, it) },
                label = { Text(property.displayName + " (YYYY-MM-DD)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                singleLine = true
            )
        }

        is PropertyEnum -> {
            // TODO: Implement ExposedDropdownMenuBox using property.validValues
            Text(text = "Enum Property: ${property.displayName} (Under Construction)", color = Color.Gray)
        }
    }
}

@Composable
private fun CollapsibleSettingsSection(
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.share_view_advanced_settings),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun SettingsSwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

enum class UnifiedSharesListItemType {
    Top, Mid, Bottom;

    @Composable
    fun getShape(): RoundedCornerShape {
        return when (this) {
            Top -> RoundedCornerShape(12.dp, 12.dp, 4.dp, 4.dp)
            Mid -> RoundedCornerShape(4.dp, 4.dp, 4.dp, 4.dp)
            Bottom -> RoundedCornerShape(4.dp, 4.dp, 12.dp, 12.dp)
        }
    }
}

@Composable
private fun UnifiedSharesListItem(
    share: Share,
    type: UnifiedSharesListItemType,
    onSelectShare: (Share) -> Unit,
    onDeleteShare: (Share) -> Unit,
    onSendEmail: (Share) -> Unit
) {
    var showContextMenu by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    ListItem(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(type.getShape())
            .combinedClickable(
                onClick = { onSelectShare(share) },
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    showContextMenu = true
                },
            )
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        headlineContent = {
            Text(
                text = "Share ${share.id}", // TODO do not hardcode
                style = MaterialTheme.typography.titleSmall
            )
        },
        supportingContent = {
            Text(
                text = share.shareState.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            Box {
                IconButton(onClick = { showContextMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }

                DropdownMenu(expanded = showContextMenu, onDismissRequest = { showContextMenu = false }) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.share_view_list_item_edit)) },
                        onClick = {
                            showContextMenu = false
                            onSelectShare(share)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.share_view_list_item_send_email)) },
                        onClick = {
                            onSendEmail(share)
                            showContextMenu = false
                        }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = {
                            Text(
                                stringResource(R.string.share_view_list_item_delete),
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            onDeleteShare(share)
                            showContextMenu = false
                        }
                    )
                }
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Preview(name = "UnifiedShareView – light", showBackground = true)
@Composable
private fun PreviewLight() {
    PreviewTheme {
        ShareView(viewModel = ShareViewModel(MockShareRepository()))
    }
}

@Preview(name = "UnifiedShareView – dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewDark() {
    PreviewTheme(darkTheme = true) {
        ShareView(viewModel = ShareViewModel(MockShareRepository()))
    }
}

@Composable
private fun PreviewTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme {
        Surface(content = content)
    }
}

fun ComposeView.setupUnifiedShare(colorScheme: ColorScheme, credentials: ServerCredentials) {
    val nextcloudHttpClient = NextcloudHttpClient.create(credentials)
    val viewModel = ShareViewModel(repository = ShareRemoteRepository(nextcloudHttpClient))

    setContent {
        MaterialTheme(
            colorScheme = colorScheme,
            content = {
                ShareView(viewModel)
            }
        )
    }
}
