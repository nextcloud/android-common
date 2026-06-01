/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */

package com.nextcloud.android.common.ui.share.component.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nextcloud.android.common.ui.R

@Composable
fun DiscardDraftShareDialog(
    onKeep: () -> Unit,
    onDiscard: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onKeep,
        title = { Text(stringResource(R.string.share_view_discard_draft_title)) },
        text = { Text(stringResource(R.string.share_view_discard_draft_message)) },
        confirmButton = {
            FilledTonalButton(onClick = onKeep) {
                Text(stringResource(R.string.share_view_discard_draft_keep))
            }
        },
        dismissButton = {
            TextButton(onClick = onDiscard) {
                Text(stringResource(R.string.share_view_discard_draft_delete))
            }
        }
    )
}