/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

@file:Suppress("FunctionNaming")

package com.nextcloud.android.common.ui.share.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

private val LEADING_ICON_SIZE = 24.dp

@Composable
fun ShareListItem(state: ShareListItemState, permissionPresets: List<PermissionPreset>, actions: ShareListItemActions) {
    val share = state.share

    Column(
        modifier = Modifier
            .fillMaxWidth(ITEM_WIDTH_FRACTION)
            .clip(state.type.getShape())
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = CONTAINER_ALPHA))
    ) {
        ListItem(
            modifier = Modifier.clickable { actions.onSelectShare(share) },
            headlineContent = {
                Text(
                    text = state.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingContent = { ShareItemLeadingContent(share = share, isExpanded = state.isExpanded) },
            supportingContent = {
                SharePermissionChip(
                    options = PermissionPresetOption.optionsFor(share, permissionPresets),
                    selectedOption = PermissionPresetOption.from(share.permissionPreset, permissionPresets),
                    onClick = { actions.onShowOverlay(ShareOverlay.QuickShare(share.id)) }
                )
            },
            trailingContent = { ShareItemTrailingIcon(state = state, actions = actions) },
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
                    }
                )
            }
        }
    }
}

@Composable
private fun ShareItemLeadingContent(share: Share, isExpanded: Boolean) {
    if (share.hasMultipleRecipients) {
        if (!isExpanded) {
            RecipientAvatarStack(recipients = share.recipients)
        }
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
private fun ShareItemTrailingIcon(state: ShareListItemState, actions: ShareListItemActions) {
    if (!state.share.hasMultipleRecipients) {
        ShareTrailingIcon(onClick = { actions.onSelectShare(state.share) })
        return
    }

    val descriptionId = if (state.isExpanded) {
        R.string.share_view_recipients_collapse
    } else {
        R.string.share_view_recipients_expand
    }

    ShareTrailingIcon(
        contentDescription = stringResource(descriptionId),
        isPointingDown = state.isExpanded,
        onClick = { actions.onToggleExpanded(state.share) }
    )
}
