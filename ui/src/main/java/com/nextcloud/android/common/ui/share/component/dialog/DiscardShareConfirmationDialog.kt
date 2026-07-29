/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */

package com.nextcloud.android.common.ui.share.component.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nextcloud.android.common.ui.R

@Composable
fun DiscardShareConfirmationDialog(
    isDraft: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val titleId = if (isDraft) {
        R.string.share_view_discard_draft_title
    } else {
        R.string.share_view_discard_changes_title
    }
    val messageId = if (isDraft) {
        R.string.share_view_discard_draft_message
    } else {
        R.string.share_view_discard_changes_message
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(titleId)) },
        text = { Text(stringResource(messageId)) },
        confirmButton = {
            FilledTonalButton(
                onClick = onConfirm,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text(stringResource(R.string.share_view_discard_confirm_action))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.share_view_discard_keep_editing_action))
            }
        }
    )
}
