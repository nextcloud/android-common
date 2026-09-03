/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

@file:Suppress("FunctionNaming", "LongMethod")

package com.nextcloud.android.common.ui.share

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.svg.SvgDecoder
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.component.ContentUnavailableView
import com.nextcloud.android.common.ui.network.auth.ServerCredentials
import com.nextcloud.android.common.ui.network.http.NextcloudHttpClient
import com.nextcloud.android.common.ui.share.component.ShareListItem
import com.nextcloud.android.common.ui.share.component.bottomsheet.AddOrEditShareBottomSheet
import com.nextcloud.android.common.ui.share.component.bottomsheet.QuickSharePermissionBottomSheet
import com.nextcloud.android.common.ui.share.component.bottomsheet.RecipientPermissionBottomSheet
import com.nextcloud.android.common.ui.share.model.api.permission.PermissionPreset
import com.nextcloud.android.common.ui.share.model.api.share.Share
import com.nextcloud.android.common.ui.share.model.ui.PermissionPresetOption
import com.nextcloud.android.common.ui.share.model.ui.ShareEditorEntry
import com.nextcloud.android.common.ui.share.model.ui.ShareItemType
import com.nextcloud.android.common.ui.share.model.ui.ShareListItemActions
import com.nextcloud.android.common.ui.share.model.ui.ShareListItemState
import com.nextcloud.android.common.ui.share.model.ui.ShareOverlay
import com.nextcloud.android.common.ui.share.model.ui.ShareScreenState
import com.nextcloud.android.common.ui.share.model.ui.recipientSummary
import com.nextcloud.android.common.ui.share.repository.ShareRemoteRepository
import com.nextcloud.android.common.ui.share.viewmodel.ShareViewModel
import com.nextcloud.android.common.ui.share.viewmodel.ShareViewModelFactory

private val FIRST_ITEM_TOP_SPACING = 16.dp
private val ITEM_SPACING = 2.dp

private val ExpandedShareIdsSaver = listSaver<Set<String>, String>(
    save = { it.toList() },
    restore = { it.toSet() }
)

@Composable
private fun ShareScreen(internalLink: String, viewModel: ShareViewModel) {
    val errorMessageId by viewModel.errorMessageId.collectAsStateWithLifecycle()
    val screenState by viewModel.state.collectAsStateWithLifecycle()
    val activeShare by viewModel.activeShare.collectAsStateWithLifecycle()
    val permissionPresets by viewModel.permissionPresets.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current
    var overlay by rememberSaveable(stateSaver = ShareOverlay.Saver) {
        mutableStateOf(ShareOverlay.None)
    }

    LaunchedEffect(errorMessageId) {
        errorMessageId?.let {
            snackbarHostState.showSnackbar(resources.getString(it))
            viewModel.updateErrorMessage(null)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.createDraftShare() }
            ) {
                Icon(painterResource(R.drawable.ic_person_add), contentDescription = "Add")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        when (val state = screenState) {
            is ShareScreenState.Loading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            else -> ShareList(
                state = state,
                permissionPresets = permissionPresets,
                paddingValues = paddingValues,
                viewModel = viewModel,
                onShowOverlay = { overlay = it }
            )
        }
    }

    activeShare.shareOrNull?.let { activeShareObject ->
        AddOrEditShareBottomSheet(
            share = activeShareObject,
            internalLink = internalLink,
            viewModel = viewModel,
            permissionPresets = permissionPresets
        )
    }

    ShareItemOverlay(
        overlay = overlay,
        shares = (screenState as? ShareScreenState.Loaded)?.shares.orEmpty(),
        permissionPresets = permissionPresets,
        onDismiss = { overlay = ShareOverlay.None },
        viewModel = viewModel
    )
}

@Composable
private fun ShareItemOverlay(
    overlay: ShareOverlay,
    shares: List<Share>,
    permissionPresets: List<PermissionPreset>,
    onDismiss: () -> Unit,
    viewModel: ShareViewModel
) {
    val share = overlay.shareId?.let { id -> shares.firstOrNull { it.id == id } } ?: return

    when (overlay) {
        is ShareOverlay.QuickShare -> QuickSharePermissionBottomSheet(
            options = PermissionPresetOption.optionsFor(share, permissionPresets),
            selectedOption = PermissionPresetOption.from(share.permissionPreset, permissionPresets),
            onOptionSelected = { option ->
                onDismiss()
                val presetClass = option.presetClass
                if (presetClass != null) {
                    viewModel.updatePermissionPreset(share.id, presetClass, updateActiveShare = false)
                } else {
                    viewModel.setActiveShare(share, ShareEditorEntry.CUSTOMIZE_PERMISSION)
                }
            },
            onDismiss = onDismiss
        )

        is ShareOverlay.RecipientPermission -> {
            val recipient = share.recipients.firstOrNull {
                it.clazz == overlay.recipientClass &&
                    it.value == overlay.recipientValue &&
                    it.instance == overlay.recipientInstance
            } ?: return

            RecipientPermissionBottomSheet(
                share = share,
                recipient = recipient,
                permissionPresets = permissionPresets,
                viewModel = viewModel,
                onDismiss = onDismiss
            )
        }

        ShareOverlay.None -> Unit
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShareList(
    state: ShareScreenState,
    permissionPresets: List<PermissionPreset>,
    paddingValues: PaddingValues,
    viewModel: ShareViewModel,
    onShowOverlay: (ShareOverlay) -> Unit
) {
    val context = LocalContext.current
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val shares = (state as? ShareScreenState.Loaded)?.shares.orEmpty()
    var expandedShareIds by rememberSaveable(stateSaver = ExpandedShareIdsSaver) { mutableStateOf(emptySet<String>()) }
    val actions = remember(viewModel, onShowOverlay) {
        ShareListItemActions(
            onSelectShare = { share -> viewModel.setActiveShare(share, ShareEditorEntry.EDIT) },
            onToggleExpanded = { share ->
                expandedShareIds = if (share.id in expandedShareIds) {
                    expandedShareIds - share.id
                } else {
                    expandedShareIds + share.id
                }
            },
            onShowOverlay = onShowOverlay
        )
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = viewModel::refreshShares,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (shares.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize()) {
                        SharePlaceholder(state)
                    }
                }
            }

            itemsIndexed(shares, key = { _, share -> share.id }) { index, share ->
                Spacer(modifier = Modifier.height(if (index == 0) FIRST_ITEM_TOP_SPACING else ITEM_SPACING))

                ShareListItem(
                    state = ShareListItemState(
                        share = share,
                        title = if (share.hasMultipleRecipients) {
                            recipientSummary(share.recipients)
                        } else {
                            share.getHeadline(context, shares)
                        },
                        type = ShareItemType.type(index, shares.lastIndex),
                        isExpanded = share.id in expandedShareIds
                    ),
                    permissionPresets = permissionPresets,
                    actions = actions
                )
            }
        }
    }
}

@Composable
private fun SharePlaceholder(state: ShareScreenState) {
    if (state is ShareScreenState.Error) {
        ContentUnavailableView(
            title = stringResource(R.string.share_view_fetch_error_message),
            description = stringResource(R.string.share_view_fetch_error_description)
        )
        return
    }

    ContentUnavailableView(
        iconId = R.drawable.ic_person_add_filled,
        title = stringResource(R.string.share_view_empty_title)
    )
}

fun ComposeView.initShareScreen(
    viewModelStoreOwner: ViewModelStoreOwner,
    sourceId: String,
    internalLink: String,
    credentials: ServerCredentials,
    colorScheme: ColorScheme
) {
    val factory = ShareViewModelFactory(sourceId) {
        ShareRemoteRepository(NextcloudHttpClient.create(credentials))
    }
    val viewModel = ViewModelProvider.create(viewModelStoreOwner, factory)[ShareViewModel::class]

    setContent {
        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context)
                .components { add(SvgDecoder.Factory()) }
                .build()
        }

        MaterialTheme(
            colorScheme = colorScheme,
            content = {
                ShareScreen(internalLink, viewModel)
            }
        )
    }
}
