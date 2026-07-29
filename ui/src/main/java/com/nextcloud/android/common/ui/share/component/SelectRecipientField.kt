/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.share.ShareViewModel
import com.nextcloud.android.common.ui.share.model.api.icon.Icon
import com.nextcloud.android.common.ui.share.model.api.recipients.Recipient
import com.nextcloud.android.common.ui.share.model.api.share.Share

private val FIELD_PADDING = 12.dp
private val CHIP_SPACING = 8.dp
private val CHIP_AVATAR_SIZE = 20.dp
private val CHIP_CLOSE_ICON_SIZE = 18.dp
private val RESULT_ICON_SIZE = 20.dp

@Composable
fun SelectRecipientField(
    share: Share,
    viewModel: ShareViewModel
) {
    val selected = share.invitedRecipients

    Column(modifier = Modifier.fillMaxWidth()) {
        RecipientChips(
            recipients = selected,
            onRemove = { recipient ->
                viewModel.removeRecipient(
                    id = share.id,
                    clazz = recipient.clazz,
                    value = recipient.value,
                    instance = recipient.instance
                )
            }
        )

        RecipientSearch(share = share, selected = selected, viewModel = viewModel)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecipientChips(recipients: List<Recipient>, onRemove: (Recipient) -> Unit) {
    if (recipients.isEmpty()) return

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = FIELD_PADDING),
        horizontalArrangement = Arrangement.spacedBy(CHIP_SPACING)
    ) {
        recipients.forEach { recipient ->
            InputChip(
                selected = false,
                onClick = { onRemove(recipient) },
                label = { Text(recipient.displayName) },
                avatar = {
                    recipient.icon?.let { RecipientIcon(icon = it, modifier = Modifier.size(CHIP_AVATAR_SIZE)) }
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.share_view_select_recipient_clear),
                        modifier = Modifier.size(CHIP_CLOSE_ICON_SIZE)
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipientSearch(share: Share, selected: List<Recipient>, viewModel: ShareViewModel) {
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    var expanded by rememberSaveable { mutableStateOf(false) }
    val results by viewModel.recipientSearchResults.collectAsStateWithLifecycle()
    val suggestions = results.filterNot { result -> selected.any { it.isSameAs(result) } }

    ExposedDropdownMenuBox(
        modifier = Modifier
            .fillMaxWidth()
            .padding(FIELD_PADDING),
        expanded = expanded && query.isNotBlank(),
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = {
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
                    results = suggestions,
                    onSelect = { recipient ->
                        viewModel.addRecipient(share.id, recipient.clazz, recipient.value, recipient.instance)
                        viewModel.onSearchQueryChanged("")
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
                            modifier = Modifier.size(RESULT_ICON_SIZE)
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
