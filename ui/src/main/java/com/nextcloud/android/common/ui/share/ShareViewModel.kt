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
import com.nextcloud.android.common.ui.share.model.ui.UnifiedShare
import com.nextcloud.android.common.ui.share.repository.ShareRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShareViewModel(
    private val repository: ShareRepository
) : ViewModel() {

    private val _shares = MutableStateFlow<List<UnifiedShare>>(emptyList())
    val shares: StateFlow<List<UnifiedShare>> = _shares

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _errorMessageId = MutableStateFlow<Int?>(null)
    val errorMessageId: StateFlow<Int?> = _errorMessageId

    init {
        loadShares()
    }

    // region private methods
    private fun loadShares() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.value = true
            _errorMessageId.value = null

            when (val result = repository.fetchShares(
                sourceType = null,
                lastShareId = null,
                limit = 50
            )) {
                is NetworkResult.Success -> _shares.update { result.data }
                is NetworkResult.ServerError,
                is NetworkResult.NetworkException -> {
                    _errorMessageId.value = R.string.share_view_fetch_error_message
                }
            }

            _loading.value = false
        }
    }
    // endregion

    // region public methods
    fun create(share: UnifiedShare) {
        viewModelScope.launch(Dispatchers.IO) {
            /*
              val request = CreateShareRequest()
            repository.createShare()
             */

        }
    }

    fun delete(share: UnifiedShare) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = share.id ?: run {
                _errorMessageId.update { R.string.share_view_delete_error_id_not_found_message }
                return@launch
            }

            when (repository.deleteShare(id)) {
                is NetworkResult.Success -> _shares.update { current -> current.filterNot { it.id == id } }
                is NetworkResult.ServerError,
                is NetworkResult.NetworkException -> {
                    _errorMessageId.update { R.string.share_view_delete_error_message }
                }
            }
        }
    }

    fun updateErrorMessage(value: Int?) {
        _errorMessageId.update {
            value
        }
    }
    // endregion
}
