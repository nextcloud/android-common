/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.network.model.NetworkResult
import com.nextcloud.android.common.ui.share.model.api.recipients.Recipient
import com.nextcloud.android.common.ui.share.model.api.request.AddRecipientRequest
import com.nextcloud.android.common.ui.share.model.api.request.AddSourceRequest
import com.nextcloud.android.common.ui.share.model.api.request.GetShareRequest
import com.nextcloud.android.common.ui.share.model.api.request.UpdateSharePermissionRequest
import com.nextcloud.android.common.ui.share.model.api.request.UpdateSharePropertyRequest
import com.nextcloud.android.common.ui.share.model.api.request.UpdateShareStateRequest
import com.nextcloud.android.common.ui.share.model.api.share.Share
import com.nextcloud.android.common.ui.share.model.api.state.ShareState
import com.nextcloud.android.common.ui.share.model.ui.ShareCategory
import com.nextcloud.android.common.ui.share.model.ui.ShareScreenState
import com.nextcloud.android.common.ui.share.model.ui.filtered
import com.nextcloud.android.common.ui.share.repository.ShareRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class ShareViewModel(
    private val repository: ShareRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ShareScreenState>(ShareScreenState.Loading)
    val state: StateFlow<ShareScreenState> = _state

    private val _activeShare = MutableStateFlow<Share?>(null)
    val activeShare: StateFlow<Share?> = _activeShare

    private val _searchQuery = MutableStateFlow("")

    private val _recipientSearchResults = MutableStateFlow<List<Recipient>>(emptyList())
    val recipientSearchResults: StateFlow<List<Recipient>> = _recipientSearchResults

    private val _errorMessageId = MutableStateFlow<Int?>(null)
    val errorMessageId: StateFlow<Int?> = _errorMessageId

    private data class PendingPropertyUpdate(val job: Job, val shareId: String, val value: String)

    private val pendingPropertyJobs = mutableMapOf<String, PendingPropertyUpdate>()

    private val _propertyErrors = MutableStateFlow<Map<String, Int?>>(emptyMap())
    val propertyErrors: StateFlow<Map<String, Int?>> = _propertyErrors

    private val currentShares: List<Share>
        get() = (_state.value as? ShareScreenState.Loaded)?.shares ?: emptyList()

    init {
        fetchShares()
        initSearchQuery()
    }

    // region search query
    @OptIn(FlowPreview::class)
    private fun initSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300L)
                .distinctUntilChanged()
                .filter { it.isNotBlank() }
                .collect { query -> executeSearch(query) }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    private suspend fun executeSearch(query: String) {
        val result = repository.fetchRecipients(null, query, 10, 0)
        if (result is NetworkResult.Success) {
            _recipientSearchResults.value = result.data
        }
    }
    // endregion

    // region shares list
    fun fetchShares(
        sourceClass: String? = null,
        lastShareID: String? = null,
        limit: Int = 50
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { ShareScreenState.Loading }
            _errorMessageId.update { null }

            val result = repository.fetchShares(sourceClass, lastShareID, limit)
            val fetched = handleResult(result, R.string.share_view_fetch_error_message) ?: return@launch
            _state.update {
                if (fetched.filtered().isEmpty()) ShareScreenState.Empty
                else ShareScreenState.Loaded(fetched)
            }
        }
    }

    fun fetchShare(id: String, request: GetShareRequest = GetShareRequest()) {
        viewModelScope.launch(Dispatchers.IO) {
            _errorMessageId.update { null }

            val result = repository.fetchShare(id, request)
            val share = handleResult(result, R.string.share_view_fetch_error_message) ?: return@launch

            _activeShare.update { share }
            replaceInList(share)
        }
    }
    // endregion

    // region create
    suspend fun createDraftShare(): Share? = withContext(Dispatchers.IO) {
        _errorMessageId.update { null }

        val result = repository.createDraftShare()
        val draft = handleResult(result, R.string.share_view_create_error_message)

        if (draft != null) {
            _activeShare.update { draft }
            _state.update { ShareScreenState.Loaded(listOf(draft) + currentShares) }
        }

        draft
    }
    // endregion

    // region state
    fun updateState(id: String, shareState: ShareState) {
        viewModelScope.launch {
            flushPendingProperties(id)
            val result = withContext(Dispatchers.IO) {
                repository.updateShareState(id, UpdateShareStateRequest(shareState))
            }
            val updated = handleResult(result, R.string.share_view_update_error_message) ?: return@launch
            if (shareState == ShareState.ACTIVE) {
                _activeShare.update { null }
            } else {
                _activeShare.update { updated }
            }
            replaceInList(updated)
        }
    }
    // endregion

    // region sources
    fun addSource(id: String, value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val clazz = "OCA\\Files\\Sharing\\Source\\NodeShareSourceType"
            val result = repository.addShareSource(id, AddSourceRequest(clazz, value))
            val updated = handleResult(result, R.string.share_view_update_error_message) ?: return@launch
            _activeShare.update { updated }
            replaceInList(updated)
        }
    }

    fun removeSource(id: String, clazz: String, value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.removeShareSource(id, clazz, value)
            val updated = handleResult(result, R.string.share_view_update_error_message) ?: return@launch
            _activeShare.update { updated }
            replaceInList(updated)
        }
    }
    // endregion

    // region recipients
    fun addRecipient(id: String, clazz: String, value: String, instance: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.addShareRecipient(id, AddRecipientRequest(clazz, value, instance))
            val updated = handleResult(result, R.string.share_view_update_error_message) ?: return@launch
            _activeShare.update { updated }
            replaceInList(updated)
        }
    }

    fun addAnyoneRecipient(category: ShareCategory, share: Share) {
        if (category != ShareCategory.Anyone) {
            return
        }

        val alreadyAdded = share.recipients.any { it.clazz == Recipient.TOKEN_RECIPIENT_CLASS }
        if (alreadyAdded) {
            return
        }

        addRecipient(
            id = share.id,
            clazz = Recipient.TOKEN_RECIPIENT_CLASS,
            value = UUID.randomUUID().toString(),
            instance = UUID.randomUUID().toString()
        )
    }

    fun removeRecipient(id: String, clazz: String, value: String, instance: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.removeShareRecipient(id, clazz, value, instance)
            val updated =  handleResult(result, R.string.share_view_update_error_message) ?: return@launch
            _activeShare.update { updated }
            replaceInList(updated)
        }
    }
    // endregion

    // region properties
    fun updateProperty(id: String, clazz: String, value: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.updateShareProperty(id, UpdateSharePropertyRequest(clazz, value))
            val updated =  handleResult(result, R.string.share_view_update_error_message) ?: return@launch
            _activeShare.update { updated }
            replaceInList(updated)
        }
    }

    fun updatePropertyDebounced(shareId: String, clazz: String, value: String) {
        pendingPropertyJobs[clazz]?.job?.cancel()
        val job = viewModelScope.launch {
            delay(PROPERTY_DEBOUNCE_DELAY_MS)
            pendingPropertyJobs.remove(clazz)
            withContext(Dispatchers.IO) { executePropertyUpdate(shareId, clazz, value) }
        }
        pendingPropertyJobs[clazz] = PendingPropertyUpdate(job, shareId, value)
    }

    private suspend fun flushPendingProperties(shareId: String) {
        val snapshot = pendingPropertyJobs.toMap()
        snapshot.values.forEach { it.job.cancel() }
        pendingPropertyJobs.clear()
        if (snapshot.isEmpty()) return
        withContext(Dispatchers.IO) {
            coroutineScope {
                snapshot.entries.map { (clazz, pending) ->
                    async { executePropertyUpdate(shareId, clazz, pending.value) }
                }.awaitAll()
            }
        }
    }

    private suspend fun executePropertyUpdate(shareId: String, clazz: String, value: String) {
        when (val result = repository.updateShareProperty(shareId, UpdateSharePropertyRequest(clazz, value))) {
            is NetworkResult.Success -> {
                _propertyErrors.update { it - clazz }
                _activeShare.update { result.data }
                replaceInList(result.data)
            }
            is NetworkResult.ServerError,
            is NetworkResult.NetworkException -> {
                _propertyErrors.update { it + (clazz to R.string.share_view_update_error_message) }
            }
        }
    }
    // endregion

    // region permissions
    fun updatePermission(id: String, clazz: String, enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.updateSharePermission(id, UpdateSharePermissionRequest(clazz, enabled))
            val updated = handleResult(result, R.string.share_view_update_error_message) ?: return@launch
            _activeShare.update { updated }
            replaceInList(updated)
        }
    }
    // endregion

    // region delete
    fun deleteShare(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.deleteShare(id)
            handleResult(result, R.string.share_view_delete_error_message) ?: return@launch

            val remaining = currentShares.filterNot { it.id == id }
            _state.update {
                if (remaining.filtered().isEmpty()) ShareScreenState.Empty
                else ShareScreenState.Loaded(remaining)
            }
            if (_activeShare.value?.id == id) _activeShare.update { null }
        }
    }
    // endregion

    // region ui helpers
    fun updateErrorMessage(value: Int?) {
        _errorMessageId.update { value }
    }

    fun setActiveShare(value: Share?) {
        _propertyErrors.update { emptyMap() }
        _activeShare.update { value }
    }
    // endregion

    // region private
    companion object {
        private const val PROPERTY_DEBOUNCE_DELAY_MS = 1000L
    }

    private fun replaceInList(updated: Share) {
        val shares = currentShares.ifEmpty { return }
        _state.update { ShareScreenState.Loaded(shares.map { if (it.id == updated.id) updated else it }) }
    }

    private fun <T> handleResult(result: NetworkResult<T>, errorId: Int): T? {
        return when (result) {
            is NetworkResult.Success -> result.data
            is NetworkResult.ServerError,
            is NetworkResult.NetworkException -> {
                _errorMessageId.update { errorId }
                null
            }
        }
    }
    // endregion
}
