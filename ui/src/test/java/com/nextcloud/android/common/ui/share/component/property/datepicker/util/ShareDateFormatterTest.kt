/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.component.property.datepicker.util

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

class ShareDateFormatterTest {

    private lateinit var originalLocale: Locale

    @Before
    fun setUp() {
        originalLocale = Locale.getDefault()
    }

    @After
    fun tearDown() {
        Locale.setDefault(originalLocale)
    }

    @Test
    fun `displays expiration date with Turkish format when device locale is Turkish`() {
        assertLocalizedDisplay(TURKISH)
    }

    @Test
    fun `displays expiration date with English format when device locale is English`() {
        assertLocalizedDisplay(ENGLISH)
    }

    @Test
    fun `displays expiration date with device locale format for other languages`() {
        assertLocalizedDisplay(GERMAN)
    }

    @Test
    fun `expiration date display differs per device locale`() {
        val turkish = displayFor(TURKISH)
        val english = displayFor(ENGLISH)
        val german = displayFor(GERMAN)

        assertNotEquals(turkish, english)
        assertNotEquals(turkish, german)
        assertNotEquals(english, german)
    }

    private fun assertLocalizedDisplay(locale: Locale) {
        val expected = DateTimeFormatter
            .ofLocalizedDate(FormatStyle.SHORT)
            .withLocale(locale)
            .format(DATE)

        assertEquals(expected, displayFor(locale))
    }

    private fun displayFor(locale: Locale): String {
        Locale.setDefault(locale)
        return ShareDateFormatter().formatIsoForDisplay(ISO_DATE)
    }

    private companion object {
        private val TURKISH: Locale = Locale.forLanguageTag("tr")
        private val ENGLISH: Locale = Locale.forLanguageTag("en")
        private val GERMAN: Locale = Locale.forLanguageTag("de")

        private const val ISO_DATE = "2026-12-31"
        private val DATE: LocalDate = LocalDate.of(2026, 12, 31)
    }
}
