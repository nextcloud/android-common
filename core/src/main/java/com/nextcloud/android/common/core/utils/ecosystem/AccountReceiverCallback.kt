/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.core.utils.ecosystem

interface AccountReceiverCallback {
    fun onAccountReceived(accountName: String)
    fun onAccountError(reason: String)
}
