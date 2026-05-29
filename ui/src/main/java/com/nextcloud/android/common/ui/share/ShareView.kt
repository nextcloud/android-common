/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.component.ContentUnavailableView
import com.nextcloud.android.common.ui.network.auth.ServerCredentials
import com.nextcloud.android.common.ui.network.http.NextcloudHttpClient
import com.nextcloud.android.common.ui.share.model.api.capabilities.SharingCapabilities
import com.nextcloud.android.common.ui.share.model.api.icon.Icon
import com.nextcloud.android.common.ui.share.model.api.property.Property
import com.nextcloud.android.common.ui.share.model.api.property.PropertyBoolean
import com.nextcloud.android.common.ui.share.model.api.property.PropertyDate
import com.nextcloud.android.common.ui.share.model.api.property.PropertyEnum
import com.nextcloud.android.common.ui.share.model.api.property.PropertyPassword
import com.nextcloud.android.common.ui.share.model.api.property.PropertyString
import com.nextcloud.android.common.ui.share.model.api.property.priority
import com.nextcloud.android.common.ui.share.model.api.share.Share
import com.nextcloud.android.common.ui.share.model.api.state.ShareState
import com.nextcloud.android.common.ui.share.model.ui.ShareCategory
import com.nextcloud.android.common.ui.share.repository.ShareRemoteRepository
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@Composable
private fun ShareView(sourceId: String, sharingCapabilities: SharingCapabilities, viewModel: ShareViewModel) {
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
                        0 -> UnifiedSharesListItemType.Top
                        shares.lastIndex -> UnifiedSharesListItemType.Bottom
                        else -> UnifiedSharesListItemType.Mid
                    }

                    if (index == 0) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    UnifiedSharesListItem(
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
                        CollapsibleSettingsSection(
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
                                        SettingsSwitchRow(
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
                CollapsibleSettingsSection(
                    label = stringResource(R.string.share_view_advanced_settings),
                    isExpanded = showAdvancedSettings,
                    onToggle = { showAdvancedSettings = !showAdvancedSettings }
                ) {
                    share.properties.sortedBy { it.priority }.forEach { property ->
                        DynamicPropertyField(share.id, property, viewModel)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipientSearchField(
    share: Share,
    viewModel: ShareViewModel
) {
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val results by viewModel.recipientSearchResults.collectAsState()
    val chipScrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (share.recipients.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(chipScrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                share.recipients.forEach { recipient ->
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
                                onClick = {
                                    viewModel.removeRecipient(
                                        id = share.id,
                                        clazz = recipient.clazz,
                                        value = recipient.value,
                                        instance = recipient.instance
                                    )
                                },
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

        ExposedDropdownMenuBox(
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
                label = { Text(stringResource(R.string.share_view_invited_category_label)) },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true)
                    .fillMaxWidth(),
                singleLine = true
            )

            if (query.isNotBlank()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
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
                                onClick = {
                                    viewModel.addRecipient(share.id, recipient.clazz, recipient.value)
                                    query = ""
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipientIcon(icon: Icon, modifier: Modifier = Modifier) {
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

@Composable
private fun DynamicPropertyField(shareId: String, property: Property, viewModel: ShareViewModel) {
    when (property) {
        is PropertyBoolean -> {
            SettingsSwitchRow(
                label = property.displayName,
                checked = property.value == "true",
                onCheckedChange = { isChecked ->
                    viewModel.updateProperty(shareId, property.clazz, isChecked.toString())
                }
            )
        }

        is PropertyString -> {
            OutlinedTextField(
                value = property.value ?: "",
                onValueChange = { viewModel.updateProperty(shareId, property.clazz, it) },
                label = { Text(property.displayName) },
                placeholder = property.hint?.let { { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                singleLine = true
            )
        }

        is PropertyPassword -> {
            OutlinedTextField(
                value = property.value ?: "",
                onValueChange = { viewModel.updateProperty(shareId, property.clazz, it) },
                label = { Text(property.displayName) },
                placeholder = property.hint?.let { { Text(it) } },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                singleLine = true
            )
        }

        is PropertyDate -> {
            // TODO: Wrap with a DatePickerDialog. Falling back to string entry for now.
            OutlinedTextField(
                value = property.value ?: "",
                onValueChange = { viewModel.updateProperty(shareId, property.clazz, it) },
                label = { Text(property.displayName + " (YYYY-MM-DD)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                singleLine = true
            )
        }

        is PropertyEnum -> {
            // TODO: Implement ExposedDropdownMenuBox using property.validValues
            Text(text = "Enum Property: ${property.displayName} (Under Construction)", color = Color.Gray)
        }
    }
}

@Composable
private fun CollapsibleSettingsSection(
    label: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column { content() }
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

enum class UnifiedSharesListItemType {
    Top, Mid, Bottom;

    @Composable
    fun getShape(): RoundedCornerShape {
        return when (this) {
            Top -> RoundedCornerShape(12.dp, 12.dp, 4.dp, 4.dp)
            Mid -> RoundedCornerShape(4.dp, 4.dp, 4.dp, 4.dp)
            Bottom -> RoundedCornerShape(4.dp, 4.dp, 12.dp, 12.dp)
        }
    }
}

@Composable
private fun UnifiedSharesListItem(
    share: Share,
    type: UnifiedSharesListItemType,
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

@Composable
private fun PreviewTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme {
        Surface(content = content)
    }
}

private val json = Json { ignoreUnknownKeys = true }

fun ComposeView.setupUnifiedShare(
    sourceId: String,
    sharingJson: String,
    credentials: ServerCredentials,
    colorScheme: ColorScheme
) {
    val sharingCapabilities =  json.decodeFromString<SharingCapabilities>(sharingJson)
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
                ShareView(sourceId, sharingCapabilities, viewModel)
            }
        )
    }
}
