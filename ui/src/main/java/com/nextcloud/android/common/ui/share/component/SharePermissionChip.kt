/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import com.nextcloud.android.common.ui.share.model.ui.PermissionPresetOption
import com.nextcloud.android.common.ui.share.model.ui.label

private val CHIP_HORIZONTAL_PADDING = 10.dp
private val CHIP_VERTICAL_PADDING = 2.dp
private val CHIP_ICON_SIZE = 18.dp
private val CHIP_ICON_SPACING = 2.dp
private const val CHIP_BACKGROUND_ALPHA = 0.08f

@Composable
fun SharePermissionChip(
    options: List<PermissionPresetOption>,
    selectedOption: PermissionPresetOption,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .offset(x = -CHIP_HORIZONTAL_PADDING)
            .clip(RoundedCornerShape(percent = 50))
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.primary.copy(alpha = CHIP_BACKGROUND_ALPHA))
            .padding(horizontal = CHIP_HORIZONTAL_PADDING, vertical = CHIP_VERTICAL_PADDING),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.CenterStart) {
            options.forEach { option ->
                Text(
                    text = option.label(),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    modifier = Modifier
                        .alpha(0f)
                        .clearAndSetSemantics {}
                )
            }

            Text(
                text = selectedOption.label(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.width(CHIP_ICON_SPACING))

        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(CHIP_ICON_SIZE)
        )
    }
}
