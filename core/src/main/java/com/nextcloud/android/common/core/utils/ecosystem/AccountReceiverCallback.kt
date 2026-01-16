/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.core.utils.ecosystem

/**
 * Callback interface for receiving account information from another Nextcloud app.
 *
 */
interface AccountReceiverCallback {
    fun onAccountReceived(accountName: String)

    fun onAccountError(reason: String)
}
