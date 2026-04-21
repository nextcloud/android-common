/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextcloud.android.common.ui.share.model.ui.UnifiedShares
import com.nextcloud.android.common.ui.share.repository.ShareRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShareViewModel(private val repository: ShareRepository): ViewModel() {
    private val _shares = MutableStateFlow<List<UnifiedShares>>(emptyList())
    val shares: StateFlow<List<UnifiedShares>> = _shares

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val shares = repository.fetchShares()
            _shares.update {
                shares
            }
        }
    }
}
