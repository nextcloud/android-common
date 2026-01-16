/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.core.utils

import com.nextcloud.android.common.core.utils.ecosystem.EcosystemManager
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.regex.Pattern

/**
 * Unit tests for validating account name format using the same
 * regular expression as {@link EcosystemManager}.
 *
 * These tests verify that:
 * - Valid account names (e.g. "abc@example.cloud.com") are accepted
 * - Invalid or malformed account names are rejected
 * - Edge cases such as empty or blank strings do not match
 *
 * The account name is expected to follow an email-like format and is
 * used when passing account information between ecosystem apps.
 */
class AccountNamePatternTest {
    private val pattern = Pattern.compile(EcosystemManager.ACCOUNT_NAME_PATTERN_REGEX)

    @Test
    fun `valid account names should match`() {
        assertTrue(pattern.matcher("abc@example.cloud.com").matches())
        assertTrue(pattern.matcher("user.name+test@sub.domain.org").matches())
        assertTrue(pattern.matcher("user_123@test.co").matches())
    }

    @Test
    fun `invalid account names should not match`() {
        assertFalse(pattern.matcher("abc").matches())
        assertFalse(pattern.matcher("abc@").matches())
        assertFalse(pattern.matcher("abc@example").matches())
        assertFalse(pattern.matcher("abc@example.").matches())
        assertFalse(pattern.matcher("abc@.com").matches())
        assertFalse(pattern.matcher("@example.com").matches())
        assertFalse(pattern.matcher("abc@example.c").matches())
        assertFalse(pattern.matcher("abc example@test.com").matches())
    }

    @Test
    fun `empty or blank account names should not match`() {
        assertFalse(pattern.matcher("").matches())
        assertFalse(pattern.matcher(" ").matches())
    }
}
