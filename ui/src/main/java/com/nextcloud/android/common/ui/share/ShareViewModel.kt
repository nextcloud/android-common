/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextcloud.android.common.ui.network.ApiResult
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

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadShares()
    }

    private fun loadShares() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.value = true
            _error.value = null

            when (val result = repository.fetchShares()) {
                is ApiResult.Success -> {
                    _shares.update { result.data }
                }

                is ApiResult.Error -> {
                    _error.value = result.error.ocs.meta.message
                }
            }

            _loading.value = false
        }
    }
}
