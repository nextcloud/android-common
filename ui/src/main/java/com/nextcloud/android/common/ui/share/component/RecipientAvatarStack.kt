/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

@file:Suppress("FunctionNaming")

package com.nextcloud.android.common.ui.share.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.share.model.api.recipients.Recipient

private const val MAX_AVATARS = 3

private val AVATAR_SIZE = 26.dp
private val AVATAR_OVERLAP = 9.dp
private val AVATAR_RING_WIDTH = 1.5.dp
private val OVERFLOW_START_PADDING = 4.dp

@Composable
fun RecipientAvatarStack(recipients: List<Recipient>, modifier: Modifier = Modifier) {
    val displayed = recipients.take(MAX_AVATARS)
    val overflow = recipients.size - displayed.size

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        displayed.forEachIndexed { index, recipient ->
            RecipientAvatar(
                recipient = recipient,
                size = AVATAR_SIZE,
                modifier = Modifier
                    .offset(x = -AVATAR_OVERLAP * index)
                    .zIndex((displayed.size - index).toFloat())
                    .border(AVATAR_RING_WIDTH, MaterialTheme.colorScheme.surface, CircleShape)
            )
        }

        if (overflow > 0) {
            Text(
                text = stringResource(R.string.share_view_recipients_overflow, overflow),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .offset(x = -AVATAR_OVERLAP * displayed.size)
                    .padding(start = OVERFLOW_START_PADDING)
            )
        }
    }
}
