/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share

import android.content.ClipData
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.share.model.ui.ShareBottomSheetState
import com.nextcloud.android.common.ui.share.model.ui.UnifiedShare
import com.nextcloud.android.common.ui.share.model.ui.UnifiedShareCategory
import com.nextcloud.android.common.ui.share.model.ui.UnifiedSharePermission
import com.nextcloud.android.common.ui.share.model.ui.customPermissionFields
import com.nextcloud.android.common.ui.network.auth.ServerCredentials
import com.nextcloud.android.common.ui.network.http.NextcloudHttpClient
import com.nextcloud.android.common.ui.share.repository.MockShareRepository
import com.nextcloud.android.common.ui.share.repository.ShareRemoteRepository
import kotlinx.coroutines.launch


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

    Scaffold(floatingActionButton = {
        FloatingActionButton(
            onClick = { bottomSheetState = ShareBottomSheetState.New(UnifiedShare.new()) },
        ) {
            Icon(painterResource(R.drawable.ic_person_add), contentDescription = "Add")
        }
    }, snackbarHost = {
        SnackbarHost(snackbarHostState)
    }) {
        LazyColumn(modifier = Modifier.padding(it)) {
            itemsIndexed(shares) { index, share ->
                val type = when (index) {
                    0 -> {
                        UnifiedSharesListItemType.Top
                    }

                    shares.lastIndex -> {
                        UnifiedSharesListItemType.Bottom
                    }

                    else -> {
                        UnifiedSharesListItemType.Mid
                    }
                }

                UnifiedSharesListItem(share, type, onSelectShare = { share ->
                    bottomSheetState = ShareBottomSheetState.Edit(share)
                }, onDeleteShare = {
                    viewModel.delete(share)
                }, onSendEmail = {
                    // TODO:
                })
            }
        }
    }

    when (bottomSheetState) {
        is ShareBottomSheetState.Edit -> {
            val state = (bottomSheetState as ShareBottomSheetState.Edit)
            AddOrEditShareBottomSheet(
                title = stringResource(R.string.share_view_bottom_sheet_edit_title, state.share.label),
                share = state.share,
                onCreateOrEdit = {

                },
                onDismiss = { bottomSheetState = ShareBottomSheetState.Idle }
            )
        }

        is ShareBottomSheetState.New -> {
            val state = (bottomSheetState as ShareBottomSheetState.New)
            AddOrEditShareBottomSheet(
                title = stringResource(R.string.share_view_bottom_sheet_new_title),
                share = state.newShare,
                onCreateOrEdit = {

                },
                onDismiss = { bottomSheetState = ShareBottomSheetState.Idle }
            )
        }

        ShareBottomSheetState.Idle -> Unit
    }
}

// TODO: Use like inner tags whenever user add a new people to the search and it
//  should look like User 1, Group 1 etc.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddOrEditShareBottomSheet(
    title: String,
    share: UnifiedShare,
    onCreateOrEdit: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()

    var category by remember { mutableStateOf(share.category) }
    var permission by remember { mutableStateOf(share.permission ?: UnifiedSharePermission.CanView) }
    var searchQuery by remember { mutableStateOf("") }
    var note by remember { mutableStateOf(share.note) }

    // Toggle states for collapse/expand
    var showInvitedSettings by remember { mutableStateOf(false) }
    var showAnyoneSettings by remember { mutableStateOf(false) }

    val clipboard = LocalClipboard.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val availablePermissions = remember {
        listOf(
            UnifiedSharePermission.CanView,
            UnifiedSharePermission.CanEdit,
            UnifiedSharePermission.FileDrop,
            UnifiedSharePermission.Custom.getFromPermission(share.permission)
        )
    }

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
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ShareCategoryButtonGroup(
                selectedCategory = category,
                onCategoryChange = { category = it }
            )

            if (category == UnifiedShareCategory.Invited) {
                InvitedShareContent(
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    permission = permission,
                    availablePermissions = availablePermissions,
                    onPermissionChange = { permission = it },
                )

                CollapsibleSettingsSection(
                    isExpanded = showInvitedSettings,
                    onToggle = { showInvitedSettings = !showInvitedSettings }
                ) {
                    InvitedInlineSettings(share)
                }
            } else {
                AnyoneShareContent(
                    permission = permission,
                    availablePermissions = availablePermissions,
                    onPermissionChange = { permission = it },
                )

                if (permission is UnifiedSharePermission.Custom) {
                    val customPermissions = permission as UnifiedSharePermission.Custom

                    customPermissionFields.forEach { field ->
                        SettingsSwitchRow(
                            label = stringResource(field.labelRes),
                            checked = field.getValue(customPermissions),
                            onCheckedChange = { permission = field.setValue(customPermissions, it) }
                        )
                    }
                }

                CollapsibleSettingsSection(
                    isExpanded = showAnyoneSettings,
                    onToggle = { showAnyoneSettings = !showAnyoneSettings }
                ) {
                    AnyoneInlineSettings(share)
                }
            }

            NoteToRecipients(note = note, onNoteChange = { note = it })

            ShareActionButtons(
                share = share,
                isSendEnabled = searchQuery.isNotBlank(),
                onCopyClick = {
                    val label = context.getString(R.string.share_view_copy_to_clipboard_label)

                    scope.launch {
                        val clipData =
                            ClipData.newPlainText(label, it)
                        clipboard.setClipEntry(clipData.toClipEntry())
                    }
                },
                onSendClick = {
                    onCreateOrEdit()
                }
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShareCategoryButtonGroup(
    selectedCategory: UnifiedShareCategory,
    onCategoryChange: (UnifiedShareCategory) -> Unit
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        UnifiedShareCategory.entries.forEachIndexed { index, option ->
            SegmentedButton(
                selected = selectedCategory == option,
                onClick = { onCategoryChange(option) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = UnifiedShareCategory.entries.size
                )
            ) {
                Text(option.name)
            }
        }
    }
}

@Composable
private fun InvitedShareContent(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    permission: UnifiedSharePermission,
    availablePermissions: List<UnifiedSharePermission>,
    onPermissionChange: (UnifiedSharePermission) -> Unit,

    ) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.share_view_invited_category_label)) },
            placeholder = { Text(stringResource(R.string.share_view_invited_category_placeholder)) },
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        PermissionDropdown(
            label = stringResource(R.string.share_view_invited_category_participants),
            selectedPermission = permission,
            availablePermissions = availablePermissions,
            onPermissionChange = onPermissionChange
        )
    }
}

@Composable
private fun NoteToRecipients(
    note: String,
    onNoteChange: (String) -> Unit
) {
    OutlinedTextField(
        value = note,
        onValueChange = onNoteChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.share_view_note_text_field_placeholder)) },
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
private fun AnyoneShareContent(
    permission: UnifiedSharePermission,
    availablePermissions: List<UnifiedSharePermission>,
    onPermissionChange: (UnifiedSharePermission) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        PermissionDropdown(
            label = stringResource(R.string.share_view_permission_dropdown_label),
            selectedPermission = permission,
            availablePermissions = availablePermissions,
            onPermissionChange = onPermissionChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PermissionDropdown(
    label: String,
    selectedPermission: UnifiedSharePermission,
    availablePermissions: List<UnifiedSharePermission>,
    onPermissionChange: (UnifiedSharePermission) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = stringResource(selectedPermission.getTextId()),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availablePermissions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(stringResource(option.getTextId())) },
                    onClick = {
                        onPermissionChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun InvitedInlineSettings(share: UnifiedShare) {
    var shareWithOthers by remember { mutableStateOf(share.recipients.isNotEmpty()) }
    var editFile by remember { mutableStateOf((share.permission as? UnifiedSharePermission.CanEdit) != null) }
    var hasExpiration by remember { mutableStateOf(false) } // TODO
    var hideDownload by remember { mutableStateOf(false) } // TODO

    SettingsSwitchRow(
        stringResource(R.string.share_view_invited_category_share_with_others_switch),
        shareWithOthers
    ) { shareWithOthers = it }
    SettingsSwitchRow(stringResource(R.string.share_view_invited_category_edit_file_switch), editFile) { editFile = it }
    SettingsSwitchRow(
        stringResource(R.string.share_view_invited_category_expiration_date_switch),
        hasExpiration
    ) { hasExpiration = it }
    SettingsSwitchRow(
        stringResource(R.string.share_view_invited_category_hide_and_download_switch),
        hideDownload
    ) { hideDownload = it }
}

@Composable
private fun AnyoneInlineSettings(share: UnifiedShare) {
    var hasPassword by remember { mutableStateOf(share.password.isNotEmpty()) }
    var hasExpiration by remember { mutableStateOf(false) }
    var limitDownloads by remember { mutableStateOf(share.limit != null) }

    var hideDownloads by remember { mutableStateOf(false) }
    var videoVerification by remember { mutableStateOf(false) }
    var showFilesInGridView by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        label = { Text(stringResource(R.string.share_view_anyone_category_label)) },
        placeholder = { Text(stringResource(R.string.share_view_anyone_category_label_placeholder)) },
        singleLine = true
    )

    SettingsSwitchRow(
        stringResource(R.string.share_view_anyone_category_expiration_date_switch),
        hasExpiration
    ) { hasExpiration = it }

    SettingsSwitchRow(
        stringResource(R.string.share_view_anyone_category_password_switch),
        hasPassword
    ) { hasPassword = it }

    SettingsSwitchRow(
        stringResource(R.string.share_view_anyone_category_limit_downloads_switch),
        limitDownloads
    ) { limitDownloads = it }

    SettingsSwitchRow(
        stringResource(R.string.share_view_anyone_category_hide_downloads_switch),
        hideDownloads
    ) { hideDownloads = it }

    SettingsSwitchRow(
        stringResource(R.string.share_view_anyone_category_video_verification_switch),
        videoVerification
    ) { videoVerification = it }

    SettingsSwitchRow(
        stringResource(R.string.share_view_anyone_category_grid_view_switch),
        showFilesInGridView
    ) { showFilesInGridView = it }
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

@Composable
private fun ShareActionButtons(
    share: UnifiedShare,
    isSendEnabled: Boolean,
    onCopyClick: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        if (share.category == UnifiedShareCategory.Invited) {
            FilledTonalButton(
                onClick = { onCopyClick("TODO") },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.share_view_copy_action))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = onSendClick,
                modifier = Modifier.weight(1f),
                enabled = isSendEnabled
            ) {
                Text(stringResource(R.string.share_view_send_action))
            }
        } else {
            Button(
                onClick = { onCopyClick("TODO") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.share_view_create_public_link))
            }
        }
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

// NOTE: To just create a public link anyone tab + just send DOES SAME THING
@Composable
private fun UnifiedSharesListItem(
    share: UnifiedShare,
    type: UnifiedSharesListItemType,
    onSelectShare: (UnifiedShare) -> Unit,
    onDeleteShare: (UnifiedShare) -> Unit,
    onSendEmail: (UnifiedShare) -> Unit
) {
    var showContextMenu by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clip(type.getShape())
            .combinedClickable(
                onClick = { onSelectShare(share) },
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    showContextMenu = true
                },
            )
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        leadingContent = {
            share.type?.let {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    it.Icon()
                }
            }
        },
        headlineContent = {
            Text(
                text = share.label,
                style = MaterialTheme.typography.titleSmall
            )
        },
        supportingContent = {
            share.permission?.getTextId()?.let {
                Text(
                    text = stringResource(it),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        trailingContent = {
            Box {
                IconButton(onClick = { showContextMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }

                DropdownMenu(
                    expanded = showContextMenu,
                    onDismissRequest = { showContextMenu = false }
                ) {
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
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
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
