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
import com.nextcloud.android.common.ui.network.model.dataOrElse
import com.nextcloud.android.common.ui.share.model.api.permission.PermissionPreset
import com.nextcloud.android.common.ui.share.model.api.recipients.Recipient
import com.nextcloud.android.common.ui.share.model.api.request.AddRecipientRequest
import com.nextcloud.android.common.ui.share.model.api.request.AddSourceRequest
import com.nextcloud.android.common.ui.share.model.api.request.GetShareRequest
import com.nextcloud.android.common.ui.share.model.api.request.UpdateSharePermissionPresetRequest
import com.nextcloud.android.common.ui.share.model.api.request.UpdateSharePermissionRequest
import com.nextcloud.android.common.ui.share.model.api.request.UpdateSharePropertyRequest
import com.nextcloud.android.common.ui.share.model.api.request.UpdateShareRecipientSecretRequest
import com.nextcloud.android.common.ui.share.model.api.request.UpdateShareStateRequest
import com.nextcloud.android.common.ui.share.model.api.share.Share
import com.nextcloud.android.common.ui.share.model.api.state.ShareState
import com.nextcloud.android.common.ui.share.model.ui.ActiveShareState
import com.nextcloud.android.common.ui.share.model.ui.ShareCategory
import com.nextcloud.android.common.ui.share.model.ui.ShareScreenState
import com.nextcloud.android.common.ui.share.model.ui.filtered
import com.nextcloud.android.common.ui.share.repository.ShareRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
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
import kotlin.time.Duration.Companion.milliseconds

class ShareViewModel(
    private val repository: ShareRepository
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 300L
        private const val PROPERTY_DEBOUNCE_DELAY = 1000L
    }

    private val _state = MutableStateFlow<ShareScreenState>(ShareScreenState.Loading)
    val state: StateFlow<ShareScreenState> = _state

    private val _activeShare = MutableStateFlow<ActiveShareState>(ActiveShareState.Dismiss)
    val activeShare: StateFlow<ActiveShareState> = _activeShare

    private val _searchQuery = MutableStateFlow("")

    private val _recipientSearchResults = MutableStateFlow<List<Recipient>>(emptyList())
    val recipientSearchResults: StateFlow<List<Recipient>> = _recipientSearchResults

    private val _errorMessageId = MutableStateFlow<Int?>(null)
    val errorMessageId: StateFlow<Int?> = _errorMessageId

    private val _propertyErrors = MutableStateFlow<Map<String, String?>>(emptyMap())
    val propertyErrors: StateFlow<Map<String, String?>> = _propertyErrors

    private val propertyUpdateJobs = mutableMapOf<String, Job>()

    private var secretUpdateJob: Job? = null

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
                .debounce(SEARCH_DEBOUNCE_DELAY.milliseconds)
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
            val fetched = result.dataOrElse { _errorMessageId.update { R.string.share_view_fetch_error_message } }
                ?: return@launch
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
            val share = result.dataOrElse { _errorMessageId.update { R.string.share_view_fetch_error_message } }
                ?: return@launch

            _activeShare.update { share.toActiveShare() }
            replaceInList(share)
        }
    }
    // endregion

    // region create
    suspend fun createDraftShare(): Share? = withContext(Dispatchers.IO) {
        _errorMessageId.update { null }

        val result = repository.createDraftShare()

        val draft = result.dataOrElse { _errorMessageId.update { R.string.share_view_create_error_message } }

        if (draft != null) {
            _activeShare.update { draft.toActiveShare() }
            _state.update { ShareScreenState.Loaded(listOf(draft) + currentShares) }
        }

        draft
    }
    // endregion

    // region state
    fun updateState(id: String, shareState: ShareState, updateAndDontDismiss: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.updateShareState(id, UpdateShareStateRequest(shareState))
            val updated = result.dataOrElse { _errorMessageId.update { R.string.share_view_update_error_message } }
                ?: return@launch
            if (shareState == ShareState.ACTIVE) {
                if (updateAndDontDismiss) {
                    _activeShare.update { ActiveShareState.Update(updated) }
                } else {
                    _activeShare.update { ActiveShareState.Dismiss }
                }
            } else {
                _activeShare.update { updated.toActiveShare() }
            }
            replaceInList(updated)
        }
    }
    // endregion

    // region sources
    fun addSource(id: String, value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // TODO pass from the clients this may vary depends on the client so notes and files for now uses this
            val clazz = "OCA\\Files\\Sharing\\Source\\NodeShareSourceType"
            val result = repository.addShareSource(id, AddSourceRequest(clazz, value))
            val updated = result.dataOrElse { _errorMessageId.update { R.string.share_view_update_error_message } }
                ?: return@launch
            _activeShare.update { updated.toActiveShare() }
            replaceInList(updated)
        }
    }

    fun removeSource(id: String, clazz: String, value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.removeShareSource(id, clazz, value)
            val updated = result.dataOrElse { _errorMessageId.update { R.string.share_view_update_error_message } }
                ?: return@launch
            _activeShare.update { updated.toActiveShare() }
            replaceInList(updated)
        }
    }
    // endregion

    // region recipients
    fun addRecipient(id: String, clazz: String, value: String, instance: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.addShareRecipient(id, AddRecipientRequest(clazz, value, instance))
            val updated = result.dataOrElse { _errorMessageId.update { R.string.share_view_update_error_message } }
                ?: return@launch
            _activeShare.update { updated.toActiveShare() }
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
            _activeShare.update { updated.toActiveShare() }
            replaceInList(updated)
        }
    }

    fun updateRecipientSecret(shareId: String, recipient: Recipient, secret: String) {
        secretUpdateJob?.cancel()
        secretUpdateJob = viewModelScope.launch(Dispatchers.IO) {
            delay(PROPERTY_DEBOUNCE_DELAY.milliseconds)
            val request = UpdateShareRecipientSecretRequest(
                clazz = recipient.clazz,
                value = recipient.value,
                instance = recipient.instance,
                secret = secret
            )
            val result = repository.updateShareRecipientSecret(shareId, request)
            val updated = result.dataOrElse { _errorMessageId.update { R.string.share_view_update_error_message } }
                ?: return@launch
            _activeShare.update { updated.toActiveShare() }
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
        propertyUpdateJobs[clazz] = viewModelScope.launch(Dispatchers.IO) {
            delay(PROPERTY_DEBOUNCE_DELAY.milliseconds)
            when (val result = repository.updateShareProperty(shareId, UpdateSharePropertyRequest(clazz, value))) {
                is NetworkResult.Success -> {
                    _propertyErrors.update { it - clazz }
                    _activeShare.update { result.data.toActiveShare() }
                    replaceInList(result.data)
                }

                is NetworkResult.ServerError -> {
                    _propertyErrors.update { it + (clazz to result.response.ocs.meta.message) }
                }

                is NetworkResult.NetworkException -> {
                    _propertyErrors.update { it + (clazz to null) }
                }
            }
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

    fun updatePermissionPreset(id: String, preset: PermissionPreset, updateActiveShare: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.updateSharePermissionPreset(id, UpdateSharePermissionPresetRequest(preset))
            val updated = result.dataOrElse { _errorMessageId.update { R.string.share_view_update_error_message } }
                ?: return@launch
            if (updateActiveShare) {
                _activeShare.update { updated.toActiveShare() }
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

            val remaining = currentShares.filterNot { it.id == id }
            _state.update {
                if (remaining.filtered().isEmpty()) ShareScreenState.Empty
                else ShareScreenState.Loaded(remaining)
            }

            if (_activeShare.value is ActiveShareState.SharedAndDismiss) {
                val sharedAndDismiss = (_activeShare.value as ActiveShareState.SharedAndDismiss)
                if (sharedAndDismiss.value.id == id) {
                    _activeShare.update { ActiveShareState.Dismiss }
                }
            }
        }
    }
    // endregion

    // region ui helpers
    fun updateErrorMessage(value: Int?) {
        _errorMessageId.update { value }
    }

    fun setActiveShare(value: Share?) {
        _propertyErrors.update { emptyMap() }

        value?.let {
            _activeShare.update { value.toActiveShare() }
        }
    }
    // endregion

    // region private
    private fun replaceInList(updated: Share) {
        val shares = currentShares.ifEmpty { return }
        _state.update { ShareScreenState.Loaded(shares.map { if (it.id == updated.id) updated else it }) }
    }
    // endregion
}
