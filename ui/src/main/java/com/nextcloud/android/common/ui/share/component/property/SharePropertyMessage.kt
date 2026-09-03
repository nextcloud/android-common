/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

@file:Suppress("FunctionNaming")

package com.nextcloud.android.common.ui.share.component.property

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val MESSAGE_START_PADDING = 16.dp
private val MESSAGE_BOTTOM_PADDING = 4.dp

@Composable
fun SharePropertyMessage(errorMessage: String?, hint: String?) {
    val message = errorMessage ?: hint?.takeIf { it.isNotBlank() } ?: return
    val color = if (errorMessage != null) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Text(
        text = message,
        color = color,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(start = MESSAGE_START_PADDING, bottom = MESSAGE_BOTTOM_PADDING)
    )
}
