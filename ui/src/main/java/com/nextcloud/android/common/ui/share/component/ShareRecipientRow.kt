/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

@file:Suppress("FunctionNaming")

package com.nextcloud.android.common.ui.share.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun ShareRecipientRow(
    share: Share,
    recipient: Recipient,
    permissionPresets: List<PermissionPreset>,
    onEditPermissions: () -> Unit
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
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}
