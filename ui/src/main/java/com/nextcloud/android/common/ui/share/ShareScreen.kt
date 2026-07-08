/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.svg.SvgDecoder
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.component.ContentUnavailableView
import com.nextcloud.android.common.ui.network.auth.ServerCredentials
import com.nextcloud.android.common.ui.network.http.NextcloudHttpClient
import com.nextcloud.android.common.ui.share.component.RecipientIcon
import com.nextcloud.android.common.ui.share.component.bottomsheet.AddOrEditShareBottomSheet
import com.nextcloud.android.common.ui.share.component.bottomsheet.QuickSharePermissionBottomSheet
import com.nextcloud.android.common.ui.share.component.dialog.DeleteShareConfirmationDialog
import com.nextcloud.android.common.ui.share.model.api.permission.PermissionPreset
import com.nextcloud.android.common.ui.share.model.api.share.Share
import com.nextcloud.android.common.ui.share.model.ui.PermissionPresetOption
import com.nextcloud.android.common.ui.share.model.ui.ShareItemOverlayState
import com.nextcloud.android.common.ui.share.model.ui.ShareItemType
import com.nextcloud.android.common.ui.share.model.ui.ShareScreenState
import com.nextcloud.android.common.ui.share.model.ui.filtered
import com.nextcloud.android.common.ui.share.repository.ShareRemoteRepository
import kotlinx.coroutines.launch

@Composable
private fun ShareScreen(sourceId: String, internalLink: String, viewModel: ShareViewModel) {
    val errorMessageId by viewModel.errorMessageId.collectAsState()
    val screenState by viewModel.state.collectAsState()
    val activeShare by viewModel.activeShare.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current
    var editorInitialPreset by remember { mutableStateOf<PermissionPresetOption?>(null) }

    LaunchedEffect(errorMessageId) {
        errorMessageId?.let {
            snackbarHostState.showSnackbar(resources.getString(it))
            viewModel.updateErrorMessage(null)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        viewModel.createDraftShare()?.let {
                            viewModel.addSource(it.id, sourceId)
                        }
                    }
                },
            ) {
                Icon(painterResource(R.drawable.ic_person_add), contentDescription = "Add")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        when (val state = screenState) {
            is ShareScreenState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ShareScreenState.Empty -> {
                ContentUnavailableView(
                    iconId = R.drawable.ic_person_add,
                    title = stringResource(R.string.share_view_empty_title),
                )
            }

            is ShareScreenState.Loaded -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    itemsIndexed(state.shares.filtered(), key = { _, share -> share.id }) { index, share ->
                        val type = ShareItemType.type(index, state.shares.lastIndex)

                        if (index == 0) {
                            Spacer(modifier = Modifier.height(16.dp))
                        } else {
                            Spacer(modifier = Modifier.height(2.dp))
                        }

                        ShareItem(
                            share = share,
                            type = type,
                            onSelectShare = { selected ->
                                editorInitialPreset = null
                                viewModel.setActiveShare(selected)
                            },
                            onCustomizeShare = { selected ->
                                editorInitialPreset = PermissionPresetOption.CUSTOM
                                viewModel.setActiveShare(selected)
                            },
                            onChangePreset = { selected, preset ->
                                viewModel.updatePermissionPreset(selected.id, preset, updateActiveShare = false)
                            },
                            onDeleteShare = { viewModel.deleteShare(it.id) },
                            onSendEmail = { selected ->
                                editorInitialPreset = null
                                viewModel.setActiveShare(selected)
                            }
                        )
                    }
                }
            }
        }
    }

    activeShare.shareOrNull?.let { activeShareObject ->
        AddOrEditShareBottomSheet(
            share = activeShareObject,
            internalLink = internalLink,
            viewModel = viewModel,
            initialPresetOption = editorInitialPreset,
            onDismissDraft = { draftShare ->
                viewModel.deleteShare(draftShare.id)
                viewModel.setActiveShare(null)
            }
        )
    }
}

@Composable
private fun ShareItem(
    share: Share,
    type: ShareItemType,
    onSelectShare: (Share) -> Unit,
    onCustomizeShare: (Share) -> Unit,
    onChangePreset: (Share, PermissionPreset) -> Unit,
    onDeleteShare: (Share) -> Unit,
    onSendEmail: (Share) -> Unit
) {
    var overlayState by remember { mutableStateOf<ShareItemOverlayState>(ShareItemOverlayState.None) }
    val haptics = LocalHapticFeedback.current

    when (overlayState) {
        is ShareItemOverlayState.QuickShare -> {
            QuickSharePermissionBottomSheet(
                selectedOption = PermissionPresetOption.from(share.permissionPreset),
                onOptionSelected = { option ->
                    overlayState = ShareItemOverlayState.None
                    val preset = option.preset
                    if (preset != null) {
                        onChangePreset(share, preset)
                    } else {
                        onCustomizeShare(share)
                    }
                },
                onDismiss = { overlayState = ShareItemOverlayState.None }
            )
        }

        is ShareItemOverlayState.DeleteConfirmation -> {
            DeleteShareConfirmationDialog(
                onConfirm = {
                    overlayState = ShareItemOverlayState.None
                    onDeleteShare(share)
                },
                onDismiss = { overlayState = ShareItemOverlayState.None }
            )
        }

        else -> Unit
    }

    ListItem(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(type.getShape())
            .combinedClickable(
                onClick = { onSelectShare(share) },
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    overlayState = ShareItemOverlayState.ContextMenu
                }
            )
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        headlineContent = {
            val headline = if (share.recipients.isNotEmpty()) {
                share.recipients.first().displayName
            } else {
                ""
            }

            Text(
                text = headline,
                style = MaterialTheme.typography.titleMedium
            )
        },
        leadingContent = {
            share.recipients.first().icon?.let { RecipientIcon(icon = it) }
        },
        supportingContent = {
            val chipHorizontalPadding = 10.dp
            Row(
                modifier = Modifier
                    .offset(x = -chipHorizontalPadding)
                    .clip(RoundedCornerShape(percent = 50))
                    .clickable { overlayState = ShareItemOverlayState.QuickShare }
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                    .padding(horizontal = chipHorizontalPadding, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(PermissionPresetOption.from(share.permissionPreset).labelRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.width(2.dp))

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        },
        trailingContent = {
            Box {
                IconButton(onClick = { overlayState = ShareItemOverlayState.ContextMenu }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }

                DropdownMenu(
                    expanded = overlayState == ShareItemOverlayState.ContextMenu,
                    onDismissRequest = { overlayState = ShareItemOverlayState.None }
                ) {
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(Icons.Default.Edit,
                                contentDescription = "Edit icon",
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        text = { Text(stringResource(R.string.share_view_list_item_edit)) },
                        onClick = {
                            overlayState = ShareItemOverlayState.None
                            onSelectShare(share)
                        }
                    )
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send icon",
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        text = { Text(stringResource(R.string.share_view_list_item_send_email)) },
                        onClick = {
                            onSendEmail(share)
                            overlayState = ShareItemOverlayState.None
                        }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete icon",
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        text = {
                            Text(
                                stringResource(R.string.share_view_list_item_delete),
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            overlayState = ShareItemOverlayState.DeleteConfirmation
                        }
                    )
                }
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

fun ComposeView.initShareScreen(
    sourceId: String,
    internalLink: String,
    credentials: ServerCredentials,
    colorScheme: ColorScheme
) {
    val nextcloudHttpClient = NextcloudHttpClient.create(credentials)
    val viewModel = ShareViewModel(repository = ShareRemoteRepository(nextcloudHttpClient))

    setContent {
        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context)
                .components { add(SvgDecoder.Factory()) }
                .build()
        }

        MaterialTheme(
            colorScheme = colorScheme,
            content = {
                ShareScreen(sourceId, internalLink, viewModel)
            }
        )
    }
}
