/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.ui

import com.nextcloud.android.common.ui.share.model.api.recipients.Recipient
import com.nextcloud.android.common.ui.share.model.api.share.Share

data class ShareListItemActions(
    val onSelectShare: (Share) -> Unit,
    val onSendEmail: (Share) -> Unit,
    val onToggleExpanded: (Share) -> Unit,
    val onShowOverlay: (ShareOverlay) -> Unit,
    val onRemoveRecipient: (Share, Recipient) -> Unit
)
