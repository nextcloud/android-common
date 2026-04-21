/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.ui

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.nextcloud.android.common.ui.R

enum class UnifiedShareType {
    InternalUser, InternalGroup, InternalLink, ExternalLink, ExternalFederated, ExternalMail;

    @Composable
    fun Icon() {
        val iconId = when (this) {
            InternalUser -> R.drawable.ic_user
            InternalGroup -> R.drawable.ic_group
            InternalLink -> R.drawable.ic_email
            ExternalLink -> R.drawable.ic_link
            ExternalFederated -> R.drawable.ic_group
            ExternalMail -> R.drawable.ic_email
        }

        Icon(painterResource(iconId), contentDescription = "share type icon")
    }
}
