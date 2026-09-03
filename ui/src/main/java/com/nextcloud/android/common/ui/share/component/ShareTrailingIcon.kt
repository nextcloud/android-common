/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

@file:Suppress("FunctionNaming")

package com.nextcloud.android.common.ui.share.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

private val TOUCH_TARGET_SIZE = 48.dp
private const val POINTING_DOWN_ROTATION = 90f
private const val POINTING_END_ROTATION = 0f

@Composable
fun ShareTrailingIcon(
    contentDescription: String? = null,
    isPointingDown: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val box = Modifier
        .size(TOUCH_TARGET_SIZE)
        .clip(CircleShape)
        .let { if (onClick == null) it else it.clickable(onClick = onClick) }

    Box(modifier = box, contentAlignment = Alignment.Center) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.rotate(
                if (isPointingDown) POINTING_DOWN_ROTATION else POINTING_END_ROTATION
            )
        )
    }
}
