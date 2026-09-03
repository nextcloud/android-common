/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.share.model.api.permission.PermissionPreset
import com.nextcloud.android.common.ui.share.model.api.recipients.Recipient
import com.nextcloud.android.common.ui.share.model.api.share.Share
import com.nextcloud.android.common.ui.share.model.ui.effectivePermissionsFor
import com.nextcloud.android.common.ui.share.model.ui.presetOption
import com.nextcloud.android.common.ui.share.model.ui.recipientPresetOptions

private val ROW_START_PADDING = 24.dp
private val AVATAR_SIZE = 24.dp
private val MENU_ICON_SIZE = 18.dp

@Composable
fun ShareRecipientRow(
    share: Share,
    recipient: Recipient,
    permissionPresets: List<PermissionPreset>,
    onEditPermissions: () -> Unit,
    onRemove: () -> Unit
) {
    ListItem(
        modifier = Modifier
            .clickable { onEditPermissions() }
            .padding(start = ROW_START_PADDING),
        headlineContent = {
            Text(
                text = recipient.displayName,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingContent = { RecipientAvatar(recipient = recipient, size = AVATAR_SIZE) },
        supportingContent = {
            SharePermissionChip(
                options = share.recipientPresetOptions(permissionPresets),
                selectedOption = share.effectivePermissionsFor(recipient).presetOption(permissionPresets),
                onClick = onEditPermissions
            )
        },
        trailingContent = { RecipientRowMenu(onRemove = onRemove) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
private fun RecipientRowMenu(onRemove: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.share_view_list_item_more_options)
            )
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(MENU_ICON_SIZE)
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.share_view_recipient_remove),
                        color = MaterialTheme.colorScheme.error
                    )
                },
                onClick = {
                    expanded = false
                    onRemove()
                }
            )
        }
    }
}
