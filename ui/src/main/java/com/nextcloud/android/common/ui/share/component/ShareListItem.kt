/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.share.model.api.permission.PermissionPreset
import com.nextcloud.android.common.ui.share.model.api.share.Share
import com.nextcloud.android.common.ui.share.model.ui.PermissionPresetOption
import com.nextcloud.android.common.ui.share.model.ui.ShareListItemActions
import com.nextcloud.android.common.ui.share.model.ui.ShareListItemState
import com.nextcloud.android.common.ui.share.model.ui.ShareOverlay

private const val ITEM_WIDTH_FRACTION = 0.9f
private const val CONTAINER_ALPHA = 0.5f
private const val COLLAPSED_CHEVRON_ROTATION = -90f
private const val EXPANDED_CHEVRON_ROTATION = 0f

private val LEADING_ICON_SIZE = 24.dp

@Composable
fun ShareListItem(state: ShareListItemState, permissionPresets: List<PermissionPreset>, actions: ShareListItemActions) {
    val share = state.share
    var contextMenuExpanded by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxWidth(ITEM_WIDTH_FRACTION)
            .clip(state.type.getShape())
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = CONTAINER_ALPHA))
    ) {
        ListItem(
            modifier = Modifier.combinedClickable(
                onClick = { actions.onSelectShare(share) },
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    contextMenuExpanded = true
                }
            ),
            headlineContent = {
                Text(
                    text = state.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingContent = { ShareItemLeadingContent(share) },
            supportingContent = {
                SharePermissionChip(
                    options = PermissionPresetOption.optionsFor(share, permissionPresets),
                    selectedOption = PermissionPresetOption.from(share.permissionPreset, permissionPresets),
                    onClick = { actions.onShowOverlay(ShareOverlay.QuickShare(share.id)) }
                )
            },
            trailingContent = {
                ShareItemTrailingContent(
                    state = state,
                    isContextMenuExpanded = contextMenuExpanded,
                    onContextMenuExpandedChange = { contextMenuExpanded = it },
                    actions = actions
                )
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )

        AnimatedVisibility(visible = state.isExpanded && share.hasMultipleRecipients) {
            ShareRecipientList(
                share = share,
                permissionPresets = permissionPresets,
                actions = actions
            )
        }
    }
}

@Composable
private fun ShareItemTrailingContent(
    state: ShareListItemState,
    isContextMenuExpanded: Boolean,
    onContextMenuExpandedChange: (Boolean) -> Unit,
    actions: ShareListItemActions
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (state.share.hasMultipleRecipients) {
            ExpandRecipientsButton(
                isExpanded = state.isExpanded,
                onClick = { actions.onToggleExpanded(state.share) }
            )
        }

        Box {
            IconButton(onClick = { onContextMenuExpandedChange(true) }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.share_view_list_item_more_options)
                )
            }

            ShareContextMenu(
                expanded = isContextMenuExpanded,
                onDismiss = { onContextMenuExpandedChange(false) },
                onEdit = { actions.onSelectShare(state.share) },
                onSendEmail = { actions.onSendEmail(state.share) },
                onDelete = { actions.onShowOverlay(ShareOverlay.DeleteConfirmation(state.share.id)) }
            )
        }
    }
}

@Composable
private fun ShareRecipientList(share: Share, permissionPresets: List<PermissionPreset>, actions: ShareListItemActions) {
    Column {
        share.recipients.forEach { recipient ->
            key(recipient.clazz, recipient.value, recipient.instance) {
                ShareRecipientRow(
                    share = share,
                    recipient = recipient,
                    permissionPresets = permissionPresets,
                    onEditPermissions = {
                        actions.onShowOverlay(
                            ShareOverlay.RecipientPermission(
                                shareId = share.id,
                                recipientClass = recipient.clazz,
                                recipientValue = recipient.value,
                                recipientInstance = recipient.instance
                            )
                        )
                    },
                    onRemove = { actions.onRemoveRecipient(share, recipient) }
                )
            }
        }
    }
}

@Composable
private fun ShareItemLeadingContent(share: Share) {
    if (share.hasMultipleRecipients) {
        RecipientAvatarStack(recipients = share.recipients)
        return
    }

    Box(
        modifier = Modifier.size(LEADING_ICON_SIZE),
        contentAlignment = Alignment.Center
    ) {
        val recipient = share.recipients.firstOrNull()
        if (recipient != null) {
            RecipientAvatar(recipient = recipient, size = LEADING_ICON_SIZE)
        } else {
            PublicLinkIcon(modifier = Modifier.size(LEADING_ICON_SIZE))
        }
    }
}

@Composable
private fun ExpandRecipientsButton(isExpanded: Boolean, onClick: () -> Unit) {
    val descriptionId = if (isExpanded) {
        R.string.share_view_recipients_collapse
    } else {
        R.string.share_view_recipients_expand
    }

    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = stringResource(descriptionId),
            modifier = Modifier.rotate(
                if (isExpanded) EXPANDED_CHEVRON_ROTATION else COLLAPSED_CHEVRON_ROTATION
            )
        )
    }
}
