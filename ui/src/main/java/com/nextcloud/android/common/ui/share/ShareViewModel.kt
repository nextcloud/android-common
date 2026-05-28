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
import com.nextcloud.android.common.ui.share.repository.ShareRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShareViewModel(
    private val repository: ShareRepository
) : ViewModel() {

    private val _shares = MutableStateFlow<List<Share>>(emptyList())
    val shares: StateFlow<List<Share>> = _shares

    private val _activeShare = MutableStateFlow<Share?>(null)
    val activeShare: StateFlow<Share?> = _activeShare

    private val _searchQuery = MutableStateFlow("")

    private val _recipientSearchResults = MutableStateFlow<List<Recipient>>(emptyList())
    val recipientSearchResults: StateFlow<List<Recipient>> = _recipientSearchResults

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _errorMessageId = MutableStateFlow<Int?>(null)
    val errorMessageId: StateFlow<Int?> = _errorMessageId

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
                .collect { query ->
                    executeSearch(query)
                }
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
        launchWithLoading {
            handleResult(
                result = repository.fetchShares(
                    sourceClass = sourceClass,
                    lastShareID = lastShareID,
                    limit = limit
                ),
                errorId = R.string.share_view_fetch_error_message
            ) { _shares.update { it } }
        }
    }

    fun fetchShare(id: String, request: GetShareRequest = GetShareRequest()) {
        launchWithLoading {
            handleResult(
                result = repository.fetchShare(id, request),
                errorId = R.string.share_view_fetch_error_message
            ) { share ->
                _activeShare.update { share }
                replaceInList(share)
            }
        }
    }
    // endregion

    // region create

    /**
     * Creates an empty draft [Share] on the server and sets it as the [activeShare].
     * Then sources can be added later.
     *
     */
    fun createShare() {
        launchWithLoading {
            handleResult(
                result = repository.createShare(),
                errorId = R.string.share_view_create_error_message
            ) { draft ->
                _activeShare.update { draft }
                _shares.update { current -> listOf(draft) + current }
            }
        }
    }
    // endregion

    // region state
    fun updateState(id: String, shareState: ShareState) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(
                result = repository.updateShareState(id, UpdateShareStateRequest(shareState)),
                errorId = R.string.share_view_update_error_message
            ) { updated ->
                _activeShare.update { updated }
                replaceInList(updated)
            }
        }
    }
    // endregion

    // region sources
    fun addSource(id: String, clazz: String, value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(
                result = repository.addShareSource(id, AddSourceRequest(clazz, value)),
                errorId = R.string.share_view_update_error_message
            ) { updated ->
                _activeShare.update { updated }
                replaceInList(updated)
            }
        }
    }

    fun removeSource(id: String, clazz: String, value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(
                result = repository.removeShareSource(id, clazz, value),
                errorId = R.string.share_view_update_error_message
            ) { updated ->
                _activeShare.update { updated }
                replaceInList(updated)
            }
        }
    }
    // endregion

    // region recipients
    fun addRecipient(id: String, clazz: String, value: String, instance: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(
                result = repository.addShareRecipient(id, AddRecipientRequest(clazz, value, instance)),
                errorId = R.string.share_view_update_error_message
            ) { updated ->
                _activeShare.update { updated }
                replaceInList(updated)
            }
        }
    }

    fun removeRecipient(id: String, clazz: String, value: String, instance: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(
                result = repository.removeShareRecipient(id, clazz, value, instance),
                errorId = R.string.share_view_update_error_message
            ) { updated ->
                _activeShare.update { updated }
                replaceInList(updated)
            }
        }
    }
    // endregion

    // region properties
    fun updateProperty(id: String, clazz: String, value: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(
                result = repository.updateShareProperty(id, UpdateSharePropertyRequest(clazz, value)),
                errorId = R.string.share_view_update_error_message
            ) { updated ->
                _activeShare.update { updated }
                replaceInList(updated)
            }
        }
    }
    // endregion

    // region permissions
    fun updatePermission(id: String, clazz: String, enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(
                result = repository.updateSharePermission(id, UpdateSharePermissionRequest(clazz, enabled)),
                errorId = R.string.share_view_update_error_message
            ) { updated ->
                _activeShare.update { updated }
                replaceInList(updated)
            }
        }
    }
    // endregion

    // region delete
    fun deleteShare(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(
                result = repository.deleteShare(id),
                errorId = R.string.share_view_delete_error_message
            ) {
                _shares.update { current -> current.filterNot { it.id == id } }
                if (_activeShare.value?.id == id) _activeShare.update { null }
            }
        }
    }
    // endregion

    // region ui helpers
    fun updateErrorMessage(value: Int?) {
        _errorMessageId.update { value }
    }

    fun setActiveShare(value: Share?) {
        _activeShare.update { value }
    }
    // endregion

    // region private
    private fun replaceInList(updated: Share) {
        _shares.update { current -> current.map { if (it.id == updated.id) updated else it } }
    }

    private fun <T> handleResult(
        result: NetworkResult<T>,
        errorId: Int,
        onSuccess: (T) -> Unit
    ) {
        when (result) {
            is NetworkResult.Success -> onSuccess(result.data)
            is NetworkResult.ServerError,
            is NetworkResult.NetworkException -> _errorMessageId.update { errorId }
        }
    }

    private fun launchWithLoading(block: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.update { true }
            _errorMessageId.update { null }
            block()
            _loading.update { false }
        }
    }
    // endregion
}
