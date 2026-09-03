/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

@file:Suppress("FunctionNaming")

package com.nextcloud.android.common.ui.share.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nextcloud.android.common.ui.share.model.api.recipients.Recipient

private val PUBLIC_LINK_ICON_PADDING = 3.dp

@Composable
fun RecipientAvatar(recipient: Recipient, size: Dp, modifier: Modifier = Modifier) {
    val avatarModifier = modifier
        .size(size)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceContainerHighest)

    val icon = recipient.icon
    if (icon == null) {
        PublicLinkIcon(modifier = avatarModifier.padding(PUBLIC_LINK_ICON_PADDING))
        return
    }

    RecipientIcon(icon = icon, modifier = avatarModifier)
}
