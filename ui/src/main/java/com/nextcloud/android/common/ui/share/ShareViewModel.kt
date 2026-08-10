/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.network.model.NetworkResult
import com.nextcloud.android.common.ui.network.model.dataOrElse
import com.nextcloud.android.common.ui.share.model.api.permission.PermissionPreset
import com.nextcloud.android.common.ui.share.model.api.recipients.Recipient
import com.nextcloud.android.common.ui.share.model.api.request.AddRecipientRequest
import com.nextcloud.android.common.ui.share.model.api.request.AddSourceRequest
import com.nextcloud.android.common.ui.share.model.api.request.UpdateSharePermissionPresetRequest
import com.nextcloud.android.common.ui.share.model.api.request.UpdateSharePermissionRequest
import com.nextcloud.android.common.ui.share.model.api.request.UpdateSharePropertyRequest
import com.nextcloud.android.common.ui.share.model.api.request.UpdateShareRecipientSecretRequest
import com.nextcloud.android.common.ui.share.model.api.request.UpdateShareStateRequest
import com.nextcloud.android.common.ui.share.model.api.share.Share
import com.nextcloud.android.common.ui.share.model.api.source.Source
import com.nextcloud.android.common.ui.share.model.api.state.ShareState
import com.nextcloud.android.common.ui.share.model.ui.ActiveShareState
import com.nextcloud.android.common.ui.share.model.ui.ShareCategory
import com.nextcloud.android.common.ui.share.model.ui.ShareEditorEntry
import com.nextcloud.android.common.ui.share.model.ui.ShareScreenState
import com.nextcloud.android.common.ui.share.model.ui.activeOnly
import com.nextcloud.android.common.ui.share.repository.ShareRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

class ShareViewModel(
    private val repository: ShareRepository,
    private val sourceId: String,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 300L
        private const val SEARCH_SUBSCRIPTION_TIMEOUT = 5_000L
        private const val RECIPIENT_SEARCH_LIMIT = 10
        private const val RECIPIENT_SEARCH_OFFSET = 0
        private const val SHARES_PAGE_SIZE = 100
        private const val MAX_SHARE_PAGES = 20
    }

    private val savedState = ShareSavedState(savedStateHandle)

    private val _state = MutableStateFlow<ShareScreenState>(ShareScreenState.Loading)
    val state: StateFlow<ShareScreenState> = _state

    private val _isPreparingLink = MutableStateFlow(false)
    val isPreparingLink: StateFlow<Boolean> = _isPreparingLink

    private val _activeShare = MutableStateFlow<ActiveShareState>(ActiveShareState.None)
    val activeShare: StateFlow<ActiveShareState> = _activeShare

    private val _editorEntry = MutableStateFlow(savedState.editorEntry)
    val editorEntry: StateFlow<ShareEditorEntry> = _editorEntry

    private val _permissionPresets = MutableStateFlow<List<PermissionPreset>>(emptyList())
    val permissionPresets: StateFlow<List<PermissionPreset>> = _permissionPresets

    private val _errorMessageId = MutableStateFlow<Int?>(null)
    val errorMessageId: StateFlow<Int?> = _errorMessageId

    private val _propertyErrors = MutableStateFlow<Map<String, String?>>(emptyMap())
    val propertyErrors: StateFlow<Map<String, String?>> = _propertyErrors

    private val _pendingProperties = MutableStateFlow<Set<String>>(emptySet())
    val pendingProperties: StateFlow<Set<String>> = _pendingProperties

    private val currentShares: List<Share>
        get() = (_state.value as? ShareScreenState.Loaded)?.shares ?: emptyList()

    private val propertyUpdateJobs = mutableMapOf<String, Job>()

    val searchQuery: StateFlow<String> = savedState.searchQuery

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val recipientSearchResults: StateFlow<List<Recipient>> = searchQuery
        .debounce(SEARCH_DEBOUNCE_DELAY.milliseconds)
        .distinctUntilChanged()
        .filter { it.isNotBlank() }
        .mapLatest { query -> searchRecipients(query) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SEARCH_SUBSCRIPTION_TIMEOUT), emptyList())

    private var secretUpdateJob: Job? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            launch { loadSharingCapabilities() }
            launch { restoreActiveShare() }
            loadShares()
        }
    }

    private suspend fun loadSharingCapabilities() {
        val result = repository.fetchSharingCapabilities()
        if (result is NetworkResult.Success) {
            _permissionPresets.update {
                result.data.permissionPresets
            }
        }
    }

    private suspend fun restoreActiveShare() {
        if (_activeShare.value != ActiveShareState.None) return
        val restored = savedState.activeShareId?.let { fetchRestorableShare(it) } ?: return

        updateActiveShare(ActiveShareState.Editing(restored))
        replaceInList(restored)
    }

    private suspend fun fetchRestorableShare(id: String): Share? {
        val result = repository.fetchShare(id)
        if (result !is NetworkResult.Success) return null
        return result.data.takeIf { savedState.activeShareId == id }
    }

    // region search query
    fun onSearchQueryChanged(query: String) {
        savedState.updateSearchQuery(query)
    }

    private suspend fun searchRecipients(query: String): List<Recipient> {
        val result = repository.fetchRecipients(null, query, RECIPIENT_SEARCH_LIMIT, RECIPIENT_SEARCH_OFFSET)
        return (result as? NetworkResult.Success)?.data ?: emptyList()
    }
    // endregion

    // region shares list
    fun refreshShares() {
        val state = _state.value as? ShareScreenState.Loaded ?: return
        if (state.refreshing) return

        viewModelScope.launch(Dispatchers.IO) {
            setRefreshing(true)
            loadShares()
            setRefreshing(false)
        }
    }

    private fun setRefreshing(refreshing: Boolean) {
        _state.update {
            if (it is ShareScreenState.Loaded) it.copy(refreshing = refreshing) else it
        }
    }

    private suspend fun loadShares() {
        _errorMessageId.update { null }
        val fetched = fetchAllShares()
        publishShares(fetched?.activeOnly() ?: currentShares)
    }

    // The server pages drafts and active shares together while the list only renders active ones,
    // so a file with a page worth of abandoned drafts would otherwise hide every real share.
    private suspend fun fetchAllShares(): List<Share>? {
        val shares = mutableListOf<Share>()
        var cursor: String? = null
        var page = 0

        while (page < MAX_SHARE_PAGES) {
            val result = repository.fetchShares(
                filterSourceTypeValue = sourceId,
                limit = SHARES_PAGE_SIZE,
                filterSourceTypeClass = Source.NODE_SOURCE_CLASS,
                lastShareID = cursor
            )
            val fetched = result.dataOrElse { _errorMessageId.update { R.string.share_view_fetch_error_message } }
                ?: return null

            shares += fetched
            if (fetched.size < SHARES_PAGE_SIZE) break

            cursor = fetched.last().id
            page++
        }

        return shares
    }
    // endregion

    // region create
    fun createDraftShare() {
        viewModelScope.launch(Dispatchers.IO) {
            _errorMessageId.update { null }

            val result = repository.createDraftShare()
            val draft = result.dataOrElse { _errorMessageId.update { R.string.share_view_create_error_message } }
                ?: return@launch

            updateEditorEntry(ShareEditorEntry.EDIT)
            updateActiveShare(draft.toActiveShare())

            applySource(draft.id, sourceId)
        }
    }
    // endregion

    // region state
    fun updateState(id: String, shareState: ShareState) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = applyState(id, shareState) ?: return@launch

            if (shareState == ShareState.ACTIVE) {
                updateActiveShare(ActiveShareState.None)
            } else {
                refreshActiveShare(updated.toActiveShare())
            }
            replaceInList(updated)
        }
    }

    fun prepareLinkForCopy(id: String) {
        if (_isPreparingLink.value) return

        viewModelScope.launch(Dispatchers.IO) {
            _isPreparingLink.update { true }

            val updated = applyState(id, ShareState.ACTIVE)
            if (updated != null) {
                refreshActiveShare(ActiveShareState.Activating(updated))
                replaceInList(updated)
            }

            _isPreparingLink.update { false }
        }
    }

    fun onLinkCopied() {
        val activating = _activeShare.value as? ActiveShareState.Activating ?: return
        updateActiveShare(ActiveShareState.Editing(activating.share))
    }

    private suspend fun applyState(id: String, shareState: ShareState): Share? {
        val result = repository.updateShareState(id, UpdateShareStateRequest(shareState))
        return result.dataOrElse { _errorMessageId.update { R.string.share_view_update_error_message } }
    }
    // endregion

    // region sources
    private suspend fun applySource(id: String, value: String) {
        val result = repository.addShareSource(id, AddSourceRequest(Source.NODE_SOURCE_CLASS, value))
        val updated = result.dataOrElse { _errorMessageId.update { R.string.share_view_update_error_message } }
            ?: return
        refreshActiveShare(updated.toActiveShare())
        replaceInList(updated)
    }
    // endregion

    // region recipients
    fun addRecipient(id: String, clazz: String, value: String, instance: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.addShareRecipient(id, AddRecipientRequest(clazz, value, instance))
            val updated = result.dataOrElse { _errorMessageId.update { R.string.share_view_update_error_message } }
                ?: return@launch
            refreshActiveShare(updated.toActiveShare())
            replaceInList(updated)
        }
    }

    fun selectCategory(category: ShareCategory, share: Share) {
        when (category) {
            ShareCategory.Anyone -> {
                removeRecipients(share) { it.clazz != Recipient.TOKEN_RECIPIENT_CLASS }
                if (share.recipients.none { it.clazz == Recipient.TOKEN_RECIPIENT_CLASS }) {
                    addRecipient(
                        id = share.id,
                        clazz = Recipient.TOKEN_RECIPIENT_CLASS,
                        value = UUID.randomUUID().toString(),
                        instance = null
                    )
                }
            }

            ShareCategory.Invited -> {
                removeRecipients(share) { it.clazz == Recipient.TOKEN_RECIPIENT_CLASS }
            }
        }
    }

    private fun removeRecipients(share: Share, predicate: (Recipient) -> Boolean) {
        share.recipients
            .filter(predicate)
            .forEach { removeRecipient(share.id, it.clazz, it.value, it.instance) }
    }

    fun removeRecipient(id: String, clazz: String, value: String, instance: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.removeShareRecipient(id, clazz, value, instance)
            val updated = result.dataOrElse { _errorMessageId.update { R.string.share_view_update_error_message } }
                ?: return@launch
            refreshActiveShare(updated.toActiveShare())
            replaceInList(updated)
        }
    }

    fun updateRecipientSecret(shareId: String, recipient: Recipient, secret: String) {
        secretUpdateJob?.cancel()
        secretUpdateJob = viewModelScope.launch(Dispatchers.IO) {
            val request = UpdateShareRecipientSecretRequest(
                clazz = recipient.clazz,
                value = recipient.value,
                instance = recipient.instance,
                secret = secret
            )
            val result = repository.updateShareRecipientSecret(shareId, request)
            val updated = result.dataOrElse { _errorMessageId.update { R.string.share_view_update_error_message } }
                ?: return@launch
            refreshActiveShare(updated.toActiveShare())
            replaceInList(updated)
        }
    }

    suspend fun generateSecret(): String? = withContext(Dispatchers.IO) {
        _errorMessageId.update { null }
        val result = repository.generateSecret()
        result.dataOrElse { _errorMessageId.update { R.string.share_view_update_error_message } }
    }
    // endregion

    // region properties
    fun updateProperty(shareId: String, clazz: String, value: String?) {
        propertyUpdateJobs[clazz]?.cancel()
        _pendingProperties.update { it + clazz }
        if (value.isNullOrEmpty()) {
            _propertyErrors.update { it - clazz }
        }
        propertyUpdateJobs[clazz] = viewModelScope.launch(Dispatchers.IO) {
            when (val result = repository.updateShareProperty(shareId, UpdateSharePropertyRequest(clazz, value))) {
                is NetworkResult.Success -> {
                    _propertyErrors.update { it - clazz }
                    refreshActiveShare(result.data.toActiveShare())
                    replaceInList(result.data)
                }

                is NetworkResult.ServerError -> {
                    if (!value.isNullOrEmpty()) {
                        _propertyErrors.update { it + (clazz to result.response.ocs.data) }
                    }
                }

                is NetworkResult.NetworkException -> {
                    if (!value.isNullOrEmpty()) {
                        _propertyErrors.update { it + (clazz to null) }
                    }
                }
            }
            _pendingProperties.update { it - clazz }
        }
    }
    // endregion

    // region permissions
    fun updatePermission(id: String, clazz: String, enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.updateSharePermission(id, UpdateSharePermissionRequest(clazz, enabled))
            val updated = result.dataOrElse { _errorMessageId.update { R.string.share_view_update_error_message } }
                ?: return@launch
            replaceInList(updated)
        }
    }

    fun updatePermissionPreset(id: String, presetClass: String, updateActiveShare: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.updateSharePermissionPreset(id, UpdateSharePermissionPresetRequest(presetClass))
            val updated = result.dataOrElse { _errorMessageId.update { R.string.share_view_update_error_message } }
                ?: return@launch
            if (updateActiveShare) {
                refreshActiveShare(updated.toActiveShare())
            }
            replaceInList(updated)
        }
    }
    // endregion

    // region delete
    fun deleteShare(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.deleteShare(id)
            result.dataOrElse { _errorMessageId.update { R.string.share_view_delete_error_message } } ?: return@launch

            publishShares(currentShares.filterNot { it.id == id })

            val editingShare = _activeShare.value
            if (editingShare is ActiveShareState.Editing && editingShare.share.id == id) {
                updateActiveShare(ActiveShareState.None)
            }
        }
    }
    // endregion

    // region ui helpers
    fun updateErrorMessage(value: Int?) {
        _errorMessageId.update { value }
    }

    fun setActiveShare(value: Share?, entry: ShareEditorEntry = ShareEditorEntry.EDIT) {
        _propertyErrors.update { emptyMap() }
        _pendingProperties.update { emptySet() }
        updateEditorEntry(entry)
        updateActiveShare(value?.toActiveShare() ?: ActiveShareState.None)
    }
    // endregion

    // region private
    private fun updateEditorEntry(entry: ShareEditorEntry) {
        _editorEntry.value = entry
        savedState.editorEntry = entry
    }

    private fun updateActiveShare(value: ActiveShareState) {
        _activeShare.value = value
        savedState.activeShareId = value.shareOrNull?.id
    }

    private fun refreshActiveShare(value: ActiveShareState) {
        val id = value.shareOrNull?.id ?: return
        if (_activeShare.value.shareOrNull?.id != id) return
        updateActiveShare(value)
    }

    // The list only ever holds active shares, so a draft being edited never reaches it and an
    // activated one is inserted rather than silently dropped.
    private fun replaceInList(updated: Share) {
        val current = currentShares
        val index = current.indexOfFirst { it.id == updated.id }
        val isActive = updated.shareState == ShareState.ACTIVE

        val shares = when {
            index >= 0 && isActive -> current.toMutableList().apply { this[index] = updated }
            index >= 0 -> current.filterNot { it.id == updated.id }
            isActive -> listOf(updated) + current
            else -> return
        }

        publishShares(shares)
    }

    private fun publishShares(shares: List<Share>) {
        _state.update {
            if (shares.isEmpty()) ShareScreenState.Empty else ShareScreenState.Loaded(shares, false)
        }
    }
    // endregion
}
