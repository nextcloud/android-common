/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.ui

import com.nextcloud.android.common.ui.R

enum class ShareCategory(val titleId: Int, val iconId: Int) {
    Invited(
        R.string.share_view_invited_category_title,
        R.drawable.ic_person_add
    ),
    Anyone(R.string.share_view_anyone_category_title, R.drawable.ic_anyone)
}
