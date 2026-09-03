/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

@file:Suppress("FunctionNaming")

package com.nextcloud.android.common.ui.share.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.share.component.dialog.DeleteShareConfirmationDialog

private val BUTTON_VERTICAL_PADDING = 8.dp
private val ICON_SIZE = 18.dp
private val ICON_SPACING = 8.dp

@Composable
fun DeleteShareButton(onDelete: () -> Unit) {
    var showConfirmation by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth().padding(vertical = BUTTON_VERTICAL_PADDING)) {
        TextButton(
            onClick = { showConfirmation = true },
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                modifier = Modifier.size(ICON_SIZE)
            )

            Spacer(modifier = Modifier.width(ICON_SPACING))

            Text(stringResource(R.string.share_view_delete_action))
        }
    }

    if (!showConfirmation) return

    DeleteShareConfirmationDialog(
        onConfirm = {
            showConfirmation = false
            onDelete()
        },
        onDismiss = { showConfirmation = false }
    )
}
