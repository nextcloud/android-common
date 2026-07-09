/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.component

import android.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.share.ShareViewModel
import com.nextcloud.android.common.ui.share.model.api.icon.Icon
import com.nextcloud.android.common.ui.share.model.api.recipients.Recipient
import com.nextcloud.android.common.ui.share.model.api.share.Share

@Composable
fun SelectRecipientField(
    share: Share,
    viewModel: ShareViewModel
) {
    val selectedRecipient = share.recipients.firstOrNull { it.clazz != Recipient.TOKEN_RECIPIENT_CLASS }

    if (selectedRecipient == null) {
        RecipientSearch(share, viewModel)
        return
    }

    SelectedRecipient(
        recipient = selectedRecipient,
        onClear = {
            viewModel.removeRecipient(
                id = share.id,
                clazz = selectedRecipient.clazz,
                value = selectedRecipient.value,
                instance = selectedRecipient.instance
            )
        }
    )
}

@Composable
private fun SelectedRecipient(recipient: Recipient, onClear: () -> Unit) {
    OutlinedTextField(
        value = recipient.displayName,
        onValueChange = {},
        readOnly = true,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        label = { Text(stringResource(R.string.share_view_select_recipient_label)) },
        leadingIcon = {
            recipient.icon?.let { RecipientIcon(icon = it, modifier = Modifier.size(24.dp)) }
        },
        trailingIcon = {
            IconButton(onClick = onClear) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.share_view_select_recipient_clear)
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipientSearch(share: Share, viewModel: ShareViewModel) {
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val results by viewModel.recipientSearchResults.collectAsState()

    ExposedDropdownMenuBox(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        expanded = expanded && query.isNotBlank(),
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                expanded = true
                viewModel.onSearchQueryChanged(it)
            },
            label = { Text(stringResource(R.string.share_view_select_recipient_label)) },
            singleLine = true,
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true)
                .fillMaxWidth()
        )

        if (query.isNotBlank()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                RecipientDropdownContent(
                    results = results,
                    onSelect = { recipient ->
                        viewModel.addRecipient(share.id, recipient.clazz, recipient.value)
                        query = ""
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun RecipientDropdownContent(
    results: List<Recipient>,
    onSelect: (Recipient) -> Unit
) {
    if (results.isEmpty()) {
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(R.string.share_view_recipient_search_field_empty_result),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            onClick = {},
            enabled = false
        )
    } else {
        results.forEach { recipient ->
            DropdownMenuItem(
                leadingIcon = {
                    recipient.icon?.let {
                        RecipientIcon(
                            icon = it,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                text = { Text(recipient.displayName) },
                onClick = { onSelect(recipient) }
            )
        }
    }
}

// TODO: can this come from backend?
@Composable
fun PublicLinkIcon(modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(R.drawable.ic_link),
        contentDescription = "public link icon",
        tint = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}

@Composable
fun RecipientIcon(icon: Icon, modifier: Modifier = Modifier) {
    val isDark = isSystemInDarkTheme()
    val url = if (isDark) icon.dark ?: icon.light else icon.light ?: icon.dark

    if (url != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .decoderFactory(SvgDecoder.Factory())
                .build(),
            contentDescription = null,
            modifier = modifier,
        )
    } else {
        // reserve the same space even with no image
        Box(modifier = modifier)
    }
}
