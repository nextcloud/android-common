/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share

import androidx.lifecycle.SavedStateHandle
import com.nextcloud.android.common.ui.share.model.ui.ShareEditorEntry
import kotlinx.coroutines.flow.StateFlow

class ShareSavedState(
    private val handle: SavedStateHandle
) {
    companion object {
        private const val KEY_ACTIVE_SHARE_ID = "active_share_id"
        private const val KEY_EDITOR_ENTRY = "editor_entry"
        private const val KEY_SEARCH_QUERY = "search_query"
    }

    var activeShareId: String?
        get() = handle[KEY_ACTIVE_SHARE_ID]
        set(value) {
            handle[KEY_ACTIVE_SHARE_ID] = value
        }

    var editorEntry: ShareEditorEntry
        get() =
            handle
                .get<String>(KEY_EDITOR_ENTRY)
                ?.let { name -> ShareEditorEntry.entries.firstOrNull { it.name == name } }
                ?: ShareEditorEntry.EDIT
        set(value) {
            handle[KEY_EDITOR_ENTRY] = value.name
        }

    val searchQuery: StateFlow<String> = handle.getStateFlow(KEY_SEARCH_QUERY, "")

    fun updateSearchQuery(query: String) {
        handle[KEY_SEARCH_QUERY] = query
    }
}
