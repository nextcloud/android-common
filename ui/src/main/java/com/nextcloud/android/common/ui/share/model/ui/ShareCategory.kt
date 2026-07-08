/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Share
import com.nextcloud.android.common.ui.R

enum class ShareCategory(val titleId: Int, val iconId: Int) {
    Invited(
        R.string.share_view_invited_category_title,
        R.drawable.ic_person_add
    ),
    Anyone(R.string.share_view_anyone_category_title, R.drawable.ic_anyone);

    val copyLinkTitleId
        get() = if (this == Invited) R.string.share_view_invited_copy_action else R.string.share_view_anyone_copy_action

    val sendActionTitleId
        get() = if (this == Invited) R.string.share_view_send_action else R.string.share_view_share_action

    val sendActionIcon
        get() = if (this == Invited) Icons.AutoMirrored.Filled.Send else Icons.Default.Share
}
