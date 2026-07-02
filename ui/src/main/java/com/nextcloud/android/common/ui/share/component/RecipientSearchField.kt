/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipientSearchField(
    share: Share,
    viewModel: ShareViewModel
) {
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val results by viewModel.recipientSearchResults.collectAsState()
    val chipScrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (share.recipients.isNotEmpty()) {
            RecipientChipRow(
                recipients = share.recipients,
                chipScrollState = chipScrollState,
                onRemove = { recipient ->
                    viewModel.removeRecipient(
                        id = share.id,
                        clazz = recipient.clazz,
                        value = recipient.value,
                        instance = recipient.instance
                    )
                }
            )
        }

        RecipientSearchDropdown(
            enabled = share.recipients.isEmpty(),
            query = query,
            expanded = expanded,
            onQueryChange = {
                query = it
                expanded = true
                viewModel.onSearchQueryChanged(it)
            },
            onExpandedChange = { expanded = it },
            onDismiss = { expanded = false }
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

@Composable
private fun RecipientChipRow(
    recipients: List<Recipient>,
    chipScrollState: androidx.compose.foundation.ScrollState,
    onRemove: (Recipient) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(chipScrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        recipients.forEach { recipient ->
            InputChip(
                selected = true,
                onClick = { },
                label = { Text(recipient.displayName) },
                leadingIcon = {
                    recipient.icon?.let {
                        RecipientIcon(
                            icon = it,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                trailingIcon = {
                    IconButton(
                        onClick = { onRemove(recipient) },
                        modifier = Modifier.size(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "remove recipient"
                        )
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipientSearchDropdown(
    enabled: Boolean,
    query: String,
    expanded: Boolean,
    onQueryChange: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded && query.isNotBlank(),
        onExpandedChange = onExpandedChange
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text(stringResource(R.string.share_view_invited_category_label)) },
            enabled = enabled,
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true)
                .fillMaxWidth(),
            singleLine = true
        )

        if (query.isNotBlank()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = onDismiss
            ) {
                content()
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
    }
}
