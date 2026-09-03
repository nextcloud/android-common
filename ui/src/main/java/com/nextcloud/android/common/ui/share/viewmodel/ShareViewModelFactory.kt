/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.nextcloud.android.common.ui.share.repository.ShareRepository

class ShareViewModelFactory(private val sourceId: String, private val repositoryProvider: () -> ShareRepository) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        require(modelClass.isAssignableFrom(ShareViewModel::class.java)) {
            "ShareViewModelFactory cannot create ${modelClass.name}"
        }

        val savedStateHandle: SavedStateHandle = extras.createSavedStateHandle()
        return ShareViewModel(repositoryProvider(), sourceId, savedStateHandle) as T
    }
}
