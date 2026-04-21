/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.previews

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextcloud.android.common.ui.share.AnyoneShareContent
import com.nextcloud.android.common.ui.share.CollapsibleSettingsSection
import com.nextcloud.android.common.ui.share.InvitedShareContent
import com.nextcloud.android.common.ui.share.NoteToRecipients
import com.nextcloud.android.common.ui.share.PermissionDropdown
import com.nextcloud.android.common.ui.share.SettingsSwitchRow
import com.nextcloud.android.common.ui.share.ShareActionButtons
import com.nextcloud.android.common.ui.share.ShareBottomSheetHeader
import com.nextcloud.android.common.ui.share.ShareCategoryButtonGroup
import com.nextcloud.android.common.ui.share.ShareViewModel
import com.nextcloud.android.common.ui.share.UnifiedShareView
import com.nextcloud.android.common.ui.share.UnifiedSharesListItem
import com.nextcloud.android.common.ui.share.UnifiedSharesListItemType
import com.nextcloud.android.common.ui.share.model.api.owner.Owner
import com.nextcloud.android.common.ui.share.model.api.user.ShareUser
import com.nextcloud.android.common.ui.share.model.ui.UnifiedShare
import com.nextcloud.android.common.ui.share.model.ui.UnifiedShareCategory
import com.nextcloud.android.common.ui.share.model.ui.UnifiedShareDownloadLimit
import com.nextcloud.android.common.ui.share.model.ui.UnifiedSharePermission
import com.nextcloud.android.common.ui.share.model.ui.UnifiedShareType
import com.nextcloud.android.common.ui.share.repository.MockShareRepository

@Composable
private fun PreviewTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme {
        Surface(content = content)
    }
}

@Preview(name = "UnifiedShareView – light", showBackground = true)
@Composable
fun Preview_UnifiedShareView_Light() {
    PreviewTheme {
        UnifiedShareView(viewModel = ShareViewModel(MockShareRepository()))
    }
}

@Preview(name = "UnifiedShareView – dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Preview_UnifiedShareView_Dark() {
    PreviewTheme(darkTheme = true) {
        UnifiedShareView(viewModel = ShareViewModel(MockShareRepository()))
    }
}

@Preview(name = "ListItem – Top", showBackground = true, group = "List Item")
@Composable
fun Preview_UnifiedSharesListItem_Top() {
    PreviewTheme {
        Column(Modifier.padding(8.dp)) {
            UnifiedSharesListItemPreviewHelper(
                share = previewUserShare(),
                type = UnifiedSharesListItemType.Top
            )
        }
    }
}

@Preview(name = "ListItem – Mid", showBackground = true, group = "List Item")
@Composable
fun Preview_UnifiedSharesListItem_Mid() {
    PreviewTheme {
        Column(Modifier.padding(8.dp)) {
            UnifiedSharesListItemPreviewHelper(
                share = previewGroupShare(),
                type = UnifiedSharesListItemType.Mid
            )
        }
    }
}

@Preview(name = "ListItem – Bottom", showBackground = true, group = "List Item")
@Composable
fun Preview_UnifiedSharesListItem_Bottom() {
    PreviewTheme {
        Column(Modifier.padding(8.dp)) {
            UnifiedSharesListItemPreviewHelper(
                share = previewPublicLinkShare(),
                type = UnifiedSharesListItemType.Bottom
            )
        }
    }
}

@Preview(name = "ListItem – Single (all three stacked)", showBackground = true, group = "List Item")
@Composable
fun Preview_UnifiedSharesListItem_AllTypes() {
    PreviewTheme {
        Column(Modifier.padding(8.dp)) {
            UnifiedSharesListItemPreviewHelper(previewUserShare(), UnifiedSharesListItemType.Top)
            UnifiedSharesListItemPreviewHelper(previewGroupShare(), UnifiedSharesListItemType.Mid)
            UnifiedSharesListItemPreviewHelper(previewPublicLinkShare(), UnifiedSharesListItemType.Bottom)
        }
    }
}

@Composable
private fun UnifiedSharesListItemPreviewHelper(share: UnifiedShare, type: UnifiedSharesListItemType) {
    UnifiedSharesListItem(share = share, type = type)
}

@Preview(name = "ListItem – CanView permission", showBackground = true, group = "Permissions")
@Composable
fun Preview_ListItem_CanView() {
    PreviewTheme {
        Column(Modifier.padding(8.dp)) {
            UnifiedSharesListItemPreviewHelper(
                share = previewUserShare(permission = UnifiedSharePermission.CanView),
                type = UnifiedSharesListItemType.Top
            )
        }
    }
}

@Preview(name = "ListItem – CanEdit permission", showBackground = true, group = "Permissions")
@Composable
fun Preview_ListItem_CanEdit() {
    PreviewTheme {
        Column(Modifier.padding(8.dp)) {
            UnifiedSharesListItemPreviewHelper(
                share = previewUserShare(permission = UnifiedSharePermission.CanEdit),
                type = UnifiedSharesListItemType.Top
            )
        }
    }
}

@Preview(name = "ListItem – FileDrop permission", showBackground = true, group = "Permissions")
@Composable
fun Preview_ListItem_FileDrop() {
    PreviewTheme {
        Column(Modifier.padding(8.dp)) {
            UnifiedSharesListItemPreviewHelper(
                share = previewUserShare(permission = UnifiedSharePermission.FileDrop),
                type = UnifiedSharesListItemType.Top
            )
        }
    }
}

@Preview(name = "ListItem – Custom permission", showBackground = true, group = "Permissions")
@Composable
fun Preview_ListItem_CustomPermission() {
    PreviewTheme {
        Column(Modifier.padding(8.dp)) {
            UnifiedSharesListItemPreviewHelper(
                share = previewUserShare(
                    permission = UnifiedSharePermission.Custom(
                         true,
                         false,
                         true,
                         false
                    )
                ),
                type = UnifiedSharesListItemType.Top
            )
        }
    }
}

@Preview(
    name = "BottomSheet – Invited / default",
    showBackground = true,
    heightDp = 900,
    group = "Bottom Sheet"
)
@Composable
fun Preview_AddShareBottomSheet_Invited() {
    PreviewTheme {
        AddShareBottomSheetContentPreview(
            category = UnifiedShareCategory.Invited,
            permission = UnifiedSharePermission.CanView,
            searchQuery = "",
            note = "",
            showSettings = false
        )
    }
}

@Preview(
    name = "BottomSheet – Invited / search filled",
    showBackground = true,
    heightDp = 900,
    group = "Bottom Sheet"
)
@Composable
fun Preview_AddShareBottomSheet_InvitedWithSearch() {
    PreviewTheme {
        AddShareBottomSheetContentPreview(
            category = UnifiedShareCategory.Invited,
            permission = UnifiedSharePermission.CanEdit,
            searchQuery = "alice@nextcloud.example",
            note = "Here are the Q2 reports!",
            showSettings = false
        )
    }
}

@Preview(
    name = "BottomSheet – Invited / settings expanded",
    showBackground = true,
    heightDp = 1100,
    group = "Bottom Sheet"
)
@Composable
fun Preview_AddShareBottomSheet_InvitedSettingsExpanded() {
    PreviewTheme {
        AddShareBottomSheetContentPreview(
            category = UnifiedShareCategory.Invited,
            permission = UnifiedSharePermission.CanView,
            searchQuery = "bob",
            note = "",
            showSettings = true
        )
    }
}

@Preview(
    name = "BottomSheet – Anyone / default",
    showBackground = true,
    heightDp = 900,
    group = "Bottom Sheet"
)
@Composable
fun Preview_AddShareBottomSheet_Anyone() {
    PreviewTheme {
        AddShareBottomSheetContentPreview(
            category = UnifiedShareCategory.Anyone,
            permission = UnifiedSharePermission.CanView,
            searchQuery = "",
            note = "",
            showSettings = false
        )
    }
}

@Preview(
    name = "BottomSheet – Anyone / Custom permission (extra switches)",
    showBackground = true,
    heightDp = 1100,
    group = "Bottom Sheet"
)
@Composable
fun Preview_AddShareBottomSheet_AnyoneCustomPermission() {
    PreviewTheme {
        AddShareBottomSheetContentPreview(
            category = UnifiedShareCategory.Anyone,
            permission = UnifiedSharePermission.Custom(
                 true,
                 false,
                 false,
                 false
            ),
            searchQuery = "",
            note = "",
            showSettings = false
        )
    }
}

@Preview(
    name = "BottomSheet – Anyone / settings expanded",
    showBackground = true,
    heightDp = 1200,
    group = "Bottom Sheet"
)
@Composable
fun Preview_AddShareBottomSheet_AnyoneSettingsExpanded() {
    PreviewTheme {
        AddShareBottomSheetContentPreview(
            category = UnifiedShareCategory.Anyone,
            permission = UnifiedSharePermission.CanView,
            searchQuery = "",
            note = "Public note",
            showSettings = true
        )
    }
}

@Preview(name = "CategoryButtons – Invited selected", showBackground = true, group = "Category Buttons")
@Composable
fun Preview_ShareCategoryButtonGroup_Invited() {
    PreviewTheme {
        Box(Modifier.padding(16.dp)) {
            ShareCategoryButtonGroup(
                selectedCategory = UnifiedShareCategory.Invited,
                onCategoryChange = {}
            )
        }
    }
}

@Preview(name = "CategoryButtons – Anyone selected", showBackground = true, group = "Category Buttons")
@Composable
fun Preview_ShareCategoryButtonGroup_Anyone() {
    PreviewTheme {
        Box(Modifier.padding(16.dp)) {
            ShareCategoryButtonGroup(
                selectedCategory = UnifiedShareCategory.Anyone,
                onCategoryChange = {}
            )
        }
    }
}

@Preview(name = "ActionButtons – Invited / Send disabled", showBackground = true, group = "Action Buttons")
@Composable
fun Preview_ShareActionButtons_InvitedSendDisabled() {
    PreviewTheme {
        Box(Modifier.padding(16.dp)) {
            ShareActionButtons(
                category = UnifiedShareCategory.Invited,
                isSendEnabled = false,
                onCopyClick = {},
                onSendClick = {}
            )
        }
    }
}

@Preview(name = "ActionButtons – Invited / Send enabled", showBackground = true, group = "Action Buttons")
@Composable
fun Preview_ShareActionButtons_InvitedSendEnabled() {
    PreviewTheme {
        Box(Modifier.padding(16.dp)) {
            ShareActionButtons(
                category = UnifiedShareCategory.Invited,
                isSendEnabled = true,
                onCopyClick = {},
                onSendClick = {}
            )
        }
    }
}

@Preview(name = "ActionButtons – Anyone", showBackground = true, group = "Action Buttons")
@Composable
fun Preview_ShareActionButtons_Anyone() {
    PreviewTheme {
        Box(Modifier.padding(16.dp)) {
            ShareActionButtons(
                category = UnifiedShareCategory.Anyone,
                isSendEnabled = false,
                onCopyClick = {},
                onSendClick = {}
            )
        }
    }
}

@Preview(name = "CollapsibleSettings – collapsed", showBackground = true, group = "Settings Section")
@Composable
fun Preview_CollapsibleSettingsSection_Collapsed() {
    PreviewTheme {
        Box(Modifier.padding(16.dp)) {
            CollapsibleSettingsSection(isExpanded = false, onToggle = {}) {
                InvitedInlineSettingsPreview()
            }
        }
    }
}

@Preview(name = "CollapsibleSettings – expanded (Invited)", showBackground = true, group = "Settings Section")
@Composable
fun Preview_CollapsibleSettingsSection_ExpandedInvited() {
    PreviewTheme {
        Box(Modifier.padding(16.dp)) {
            CollapsibleSettingsSection(isExpanded = true, onToggle = {}) {
                InvitedInlineSettingsPreview()
            }
        }
    }
}

@Preview(name = "CollapsibleSettings – expanded (Anyone)", showBackground = true, group = "Settings Section")
@Composable
fun Preview_CollapsibleSettingsSection_ExpandedAnyone() {
    PreviewTheme {
        Box(Modifier.padding(16.dp)) {
            CollapsibleSettingsSection(isExpanded = true, onToggle = {}) {
                AnyoneInlineSettingsPreview()
            }
        }
    }
}

private val allPermissions = listOf(
    UnifiedSharePermission.CanView,
    UnifiedSharePermission.CanEdit,
    UnifiedSharePermission.FileDrop,
    UnifiedSharePermission.Custom(false, false, false, false)
)

@Preview(name = "PermissionDropdown – CanView", showBackground = true, group = "Permission Dropdown")
@Composable
fun Preview_PermissionDropdown_CanView() {
    PreviewTheme {
        Box(Modifier.padding(16.dp)) {
            PermissionDropdown(
                label = "Participants",
                selectedPermission = UnifiedSharePermission.CanView,
                availablePermissions = allPermissions,
                onPermissionChange = {}
            )
        }
    }
}

@Preview(name = "PermissionDropdown – CanEdit", showBackground = true, group = "Permission Dropdown")
@Composable
fun Preview_PermissionDropdown_CanEdit() {
    PreviewTheme {
        Box(Modifier.padding(16.dp)) {
            PermissionDropdown(
                label = "Anyone with the link",
                selectedPermission = UnifiedSharePermission.CanEdit,
                availablePermissions = allPermissions,
                onPermissionChange = {}
            )
        }
    }
}

@Preview(name = "PermissionDropdown – FileDrop", showBackground = true, group = "Permission Dropdown")
@Composable
fun Preview_PermissionDropdown_FileDrop() {
    PreviewTheme {
        Box(Modifier.padding(16.dp)) {
            PermissionDropdown(
                label = "Anyone with the link",
                selectedPermission = UnifiedSharePermission.FileDrop,
                availablePermissions = allPermissions,
                onPermissionChange = {}
            )
        }
    }
}

@Preview(name = "PermissionDropdown – Custom", showBackground = true, group = "Permission Dropdown")
@Composable
fun Preview_PermissionDropdown_Custom() {
    PreviewTheme {
        Box(Modifier.padding(16.dp)) {
            PermissionDropdown(
                label = "Participants",
                selectedPermission = UnifiedSharePermission.Custom(true, false, false, false),
                availablePermissions = allPermissions,
                onPermissionChange = {}
            )
        }
    }
}

@Preview(name = "NoteToRecipients – empty", showBackground = true, group = "Note")
@Composable
fun Preview_NoteToRecipients_Empty() {
    PreviewTheme {
        Box(Modifier.padding(16.dp)) {
            NoteToRecipients(note = "", onNoteChange = {})
        }
    }
}

@Preview(name = "NoteToRecipients – with text", showBackground = true, group = "Note")
@Composable
fun Preview_NoteToRecipients_WithText() {
    PreviewTheme {
        Box(Modifier.padding(16.dp)) {
            NoteToRecipients(note = "Please review by end of week!", onNoteChange = {})
        }
    }
}

@Preview(name = "InvitedShareContent – empty query", showBackground = true, group = "Invited Content")
@Composable
fun Preview_InvitedShareContent_EmptyQuery() {
    PreviewTheme {
        Box(Modifier.padding(16.dp)) {
            InvitedShareContent(
                searchQuery = "",
                onSearchChange = {},
                permission = UnifiedSharePermission.CanView,
                availablePermissions = allPermissions,
                onPermissionChange = {}
            )
        }
    }
}

@Preview(name = "InvitedShareContent – with query", showBackground = true, group = "Invited Content")
@Composable
fun Preview_InvitedShareContent_WithQuery() {
    PreviewTheme {
        Box(Modifier.padding(16.dp)) {
            InvitedShareContent(
                searchQuery = "carol@company.org",
                onSearchChange = {},
                permission = UnifiedSharePermission.CanEdit,
                availablePermissions = allPermissions,
                onPermissionChange = {}
            )
        }
    }
}

@Preview(name = "AnyoneShareContent – CanView", showBackground = true, group = "Anyone Content")
@Composable
fun Preview_AnyoneShareContent_CanView() {
    PreviewTheme {
        Box(Modifier.padding(16.dp)) {
            AnyoneShareContent(
                permission = UnifiedSharePermission.CanView,
                availablePermissions = allPermissions,
                onPermissionChange = {}
            )
        }
    }
}

@Preview(name = "AnyoneShareContent – FileDrop", showBackground = true, group = "Anyone Content")
@Composable
fun Preview_AnyoneShareContent_FileDrop() {
    PreviewTheme {
        Box(Modifier.padding(16.dp)) {
            AnyoneShareContent(
                permission = UnifiedSharePermission.FileDrop,
                availablePermissions = allPermissions,
                onPermissionChange = {}
            )
        }
    }
}

@Preview(name = "SwitchRow – off", showBackground = true, group = "Switch Row")
@Composable
fun Preview_SettingsSwitchRow_Off() {
    PreviewTheme {
        Box(Modifier.padding(horizontal = 16.dp)) {
            SettingsSwitchRow(label = "Hide download and sync options", checked = false, onCheckedChange = {})
        }
    }
}

@Preview(name = "SwitchRow – on", showBackground = true, group = "Switch Row")
@Composable
fun Preview_SettingsSwitchRow_On() {
    PreviewTheme {
        Box(Modifier.padding(horizontal = 16.dp)) {
            SettingsSwitchRow(label = "Expiration date", checked = true, onCheckedChange = {})
        }
    }
}

@Preview(name = "Item shape – all types", showBackground = true, group = "Shape")
@Composable
fun Preview_ItemShapes_AllTypes() {
    PreviewTheme {
        Column(Modifier.padding(8.dp)) {
            UnifiedSharesListItemType.entries.forEach { type ->
                UnifiedSharesListItemPreviewHelper(share = previewUserShare(), type = type)
            }
        }
    }
}

@Preview(
    name = "UnifiedShareView – tablet landscape",
    showBackground = true,
    widthDp = 840,
    heightDp = 600
)
@Composable
fun Preview_UnifiedShareView_Tablet() {
    PreviewTheme {
        UnifiedShareView(viewModel = ShareViewModel(MockShareRepository()))
    }
}

@Composable
private fun AddShareBottomSheetContentPreview(
    category: UnifiedShareCategory,
    permission: UnifiedSharePermission,
    searchQuery: String,
    note: String,
    showSettings: Boolean
) {
    val availablePermissions = listOf(
        UnifiedSharePermission.CanView,
        UnifiedSharePermission.CanEdit,
        UnifiedSharePermission.FileDrop,
        UnifiedSharePermission.Custom(false, false, false, false)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ShareBottomSheetHeader(filename = "Abc.txt")

        ShareCategoryButtonGroup(
            selectedCategory = category,
            onCategoryChange = {}
        )

        if (category == UnifiedShareCategory.Invited) {
            InvitedShareContent(
                searchQuery = searchQuery,
                onSearchChange = {},
                permission = permission,
                availablePermissions = availablePermissions,
                onPermissionChange = {}
            )
            CollapsibleSettingsSection(
                isExpanded = showSettings,
                onToggle = {}
            ) {
                InvitedInlineSettingsPreview()
            }
        } else {
            AnyoneShareContent(
                permission = permission,
                availablePermissions = availablePermissions,
                onPermissionChange = {}
            )
            if (permission is UnifiedSharePermission.Custom) {
                SettingsSwitchRow("View files", false) {}
                SettingsSwitchRow("Edit files", false) {}
                SettingsSwitchRow("Create files", false) {}
                SettingsSwitchRow("Delete files", false) {}
            }
            CollapsibleSettingsSection(
                isExpanded = showSettings,
                onToggle = {}
            ) {
                AnyoneInlineSettingsPreview()
            }
        }

        NoteToRecipients(note = note, onNoteChange = {})

        ShareActionButtons(
            category = category,
            isSendEnabled = searchQuery.isNotBlank(),
            onCopyClick = {},
            onSendClick = {}
        )
    }
}

@Composable
private fun InvitedInlineSettingsPreview() {
    Column {
        SettingsSwitchRow("Share with others", false) {}
        SettingsSwitchRow("Edit file", false) {}
        SettingsSwitchRow("Expiration date", true) {}
        SettingsSwitchRow("Hide download and sync options", false) {}
    }
}

@Composable
private fun AnyoneInlineSettingsPreview() {
    Column {
        OutlinedTextField(
            value = "Public reports link",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            label = { Text("Label") },
            placeholder = { Text("Optional name for this link") },
            singleLine = true
        )
        SettingsSwitchRow("Expiration date", false) {}
        SettingsSwitchRow("Password", true) {}
        SettingsSwitchRow("Limit downloads", false) {}
        SettingsSwitchRow("Hide downloads", false) {}
        SettingsSwitchRow("Video verification", false) {}
        SettingsSwitchRow("Show files in grid view", true) {}
    }
}

private fun previewUserShare(
    permission: UnifiedSharePermission = UnifiedSharePermission.CanView
) = UnifiedShare(
    id = "1",
    sources = listOf(
        ShareUser(
            type = "user",
            value = "alice@company.com",
            displayName = "Alice Smith"
        )
    ),
    recipients = emptyList(),
    properties = emptyMap(),
    lastUpdated = 0,
    owner = Owner(
        userId = "alice",
        displayName = "Alice Smith"
    ),
    label = "Alice Smith",
    type = UnifiedShareType.InternalUser,
    permission = permission,
    category = UnifiedShareCategory.Invited,
    note = "",
    password = "",
    limit = UnifiedShareDownloadLimit(0, 0)
)

private fun previewGroupShare(
    permission: UnifiedSharePermission = UnifiedSharePermission.CanEdit
) = UnifiedShare(
    id = "2",
    sources = listOf(
        ShareUser(
            type = "group",
            value = "design",
            displayName = "Design Team"
        )
    ),
    recipients = emptyList(),
    properties = emptyMap(),
    lastUpdated = 0,
    owner = Owner(
        userId = "system",
        displayName = "System"
    ),
    label = "Design Team",
    type = UnifiedShareType.InternalGroup,
    permission = permission,
    category = UnifiedShareCategory.Invited,
    note = "",
    password = "",
    limit = UnifiedShareDownloadLimit(0, 0)
)

private fun previewPublicLinkShare(
    permission: UnifiedSharePermission = UnifiedSharePermission.FileDrop
) = UnifiedShare(
    id = "3",
    sources = listOf(
        ShareUser(
            type = "link",
            value = "https://nextcloud.com/s/abc123",
            displayName = "Public Link"
        )
    ),
    recipients = emptyList(),
    properties = emptyMap(),
    lastUpdated = 1710000000,
    owner = Owner(
        userId = "system",
        displayName = "System"
    ),
    label = "Public link",
    type = UnifiedShareType.ExternalLink,
    permission = permission,
    category = UnifiedShareCategory.Anyone,
    note = "",
    password = "1234",
    limit = UnifiedShareDownloadLimit(50, 5)
)
