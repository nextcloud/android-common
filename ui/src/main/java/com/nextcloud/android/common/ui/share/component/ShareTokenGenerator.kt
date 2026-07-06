/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.component

import java.security.SecureRandom

object ShareTokenGenerator {
    const val MAX_LENGTH = 32
    private const val DEFAULT_LENGTH = 15
    private const val HUMAN_READABLE_CHARS = "abcdefgijkmnopqrstwxyzABCDEFGHJKLMNPQRSTWXYZ23456789"

    private val random = SecureRandom()

    fun generate(length: Int = DEFAULT_LENGTH): String = buildString {
        repeat(length) { append(HUMAN_READABLE_CHARS[random.nextInt(HUMAN_READABLE_CHARS.length)]) }
    }
}
