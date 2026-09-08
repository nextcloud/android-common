/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Alper Ozturk <alper.ozturk@nextcloud.com>
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api.share

object ShareCreationOrder : Comparator<Share> {
    override fun compare(first: Share, second: Share): Int {
        val firstId = first.id.toBigIntegerOrNull()
        val secondId = second.id.toBigIntegerOrNull()

        if (firstId == null || secondId == null) {
            return first.id.compareTo(second.id)
        }

        return firstId.compareTo(secondId)
    }
}
