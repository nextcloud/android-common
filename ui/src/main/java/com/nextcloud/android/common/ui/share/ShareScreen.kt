/*
 * Nextcloud - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Alper Ozturk <alper.ozturk@nextcloud.com>
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */

package com.nextcloud.android.common.ui.share

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
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
import com.nextcloud.android.common.ui.share.component.CollapsibleShareSection
import com.nextcloud.android.common.ui.share.component.PropertyView
import com.nextcloud.android.common.ui.share.component.RecipientSearchField
import com.nextcloud.android.common.ui.share.component.ShareSwitch
import com.nextcloud.android.common.ui.share.model.api.capabilities.SharingCapabilities
import com.nextcloud.android.common.ui.share.model.api.property.priority
import com.nextcloud.android.common.ui.share.model.api.share.Share
import com.nextcloud.android.common.ui.share.model.api.state.ShareState
import com.nextcloud.android.common.ui.share.model.ui.ShareCategory
import com.nextcloud.android.common.ui.share.model.ui.ShareItemType
import com.nextcloud.android.common.ui.share.repository.ShareRemoteRepository
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@Composable
private fun ShareScreen(sourceId: String, sharingCapabilities: SharingCapabilities, viewModel: ShareViewModel) {
    val errorMessageId by viewModel.errorMessageId.collectAsState()
    val shares by viewModel.shares.collectAsState()
    val activeShare by viewModel.activeShare.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val filteredShares = shares.filter { it.shareState != ShareState.DRAFT }

    LaunchedEffect(errorMessageId) {
        errorMessageId?.let {
            snackbarHostState.showSnackbar(context.getString(it))
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
        if (filteredShares.isEmpty()) {
            ContentUnavailableView(
                iconId = R.drawable.ic_person_add,
                title =
                    stringResource(R.string.share_view_empty_title),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(filteredShares) { index, share ->
                    val type = when (index) {
                        0 -> ShareItemType.Top
                        shares.lastIndex -> ShareItemType.Bottom
                        else -> ShareItemType.Mid
                    }

                    if (index == 0) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    ShareItem(
                        share = share,
                        type = type,
                        onSelectShare = { selected ->
                            viewModel.setActiveShare(selected)
                        },
                        onDeleteShare = { viewModel.deleteShare(it.id) },
                        onSendEmail = { }
                    )
                }
            }
        }
    }

    activeShare?.let {
        AddOrEditShareBottomSheet(
            share = it,
            sharingCapabilities = sharingCapabilities,
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddOrEditShareBottomSheet(
    share: Share,
    sharingCapabilities: SharingCapabilities,
    viewModel: ShareViewModel
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val categories = remember { ShareCategory.entries.toList() }
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var showAdvancedSettings by remember { mutableStateOf(false) }
    var expandedCategories by remember { mutableStateOf(emptySet<String>()) }

    ModalBottomSheet(
        onDismissRequest = {
            if (share.shareState == ShareState.DRAFT) {
                viewModel.deleteShare(share.id)
            }
            viewModel.setActiveShare(null)
        },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = share.title(context),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                categories.forEachIndexed { index, category ->
                    SegmentedButton(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = categories.size)
                    ) {
                        Text(category.name)
                    }
                }
            }

            if (selectedCategory == ShareCategory.Invited) {
                RecipientSearchField(share, viewModel)
            }

            sharingCapabilities.permissionCategories
                .sortedBy { it.priority }
                .forEach { sharingCapability ->
                    key(sharingCapability.class_field) {
                        CollapsibleShareSection(
                            label = sharingCapability.displayName,
                            isExpanded = sharingCapability.displayName in expandedCategories,
                            onToggle = {
                                expandedCategories = if (expandedCategories.contains(sharingCapability.displayName)) {
                                    expandedCategories - sharingCapability.displayName
                                } else {
                                    expandedCategories + sharingCapability.displayName
                                }
                            },
                        ) {
                            share.permissions
                                .filter { permission -> permission.category == sharingCapability.class_field }
                                .sortedBy { it.displayName }
                                .forEach { permission ->
                                    key(permission.clazz) {
                                        ShareSwitch(
                                            label = permission.displayName,
                                            checked = permission.enabled,
                                            onCheckedChange = { isChecked ->
                                                viewModel.updatePermission(share.id, permission.clazz, isChecked)
                                            }
                                        )
                                    }
                                }
                        }
                    }
                }

            if (share.properties.isNotEmpty()) {
                CollapsibleShareSection(
                    label = stringResource(R.string.share_view_advanced_settings),
                    isExpanded = showAdvancedSettings,
                    onToggle = { showAdvancedSettings = !showAdvancedSettings }
                ) {
                    share.properties.sortedBy { it.priority }.forEach { property ->
                        PropertyView(share.id, property, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun ShareItem(
    share: Share,
    type: ShareItemType,
    onSelectShare: (Share) -> Unit,
    onDeleteShare: (Share) -> Unit,
    onSendEmail: (Share) -> Unit
) {
    var showContextMenu by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    ListItem(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(type.getShape())
            .combinedClickable(
                onClick = { onSelectShare(share) },
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    showContextMenu = true
                },
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
                style = MaterialTheme.typography.titleSmall
            )
        },
        supportingContent = {
            Text(
                text = share.shareState.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            Box {
                IconButton(onClick = { showContextMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }

                DropdownMenu(expanded = showContextMenu, onDismissRequest = { showContextMenu = false }) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.share_view_list_item_edit)) },
                        onClick = {
                            showContextMenu = false
                            onSelectShare(share)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.share_view_list_item_send_email)) },
                        onClick = {
                            onSendEmail(share)
                            showContextMenu = false
                        }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = {
                            Text(
                                stringResource(R.string.share_view_list_item_delete),
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            onDeleteShare(share)
                            showContextMenu = false
                        }
                    )
                }
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

private val json = Json { ignoreUnknownKeys = true }

fun ComposeView.setupUnifiedShare(
    sourceId: String,
    sharingJson: String,
    credentials: ServerCredentials,
    colorScheme: ColorScheme
) {
    val sharingCapabilities = json.decodeFromString<SharingCapabilities>(sharingJson)
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
                ShareScreen(sourceId, sharingCapabilities, viewModel)
            }
        )
    }
}
