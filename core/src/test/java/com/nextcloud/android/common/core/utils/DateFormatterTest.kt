/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2025 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.core.utils

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.nextcloud.android.common.core.R
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * DateFormatter tests checking the different formats relative/absolute date formatting.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.Q])
class DateFormatterTest {
    private lateinit var dateFormatter: DateFormatter
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        // Force a specific locale to make tests independent of the test environment's locale
        val config = context.resources.configuration
        config.setLocale(Locale.US)
        val localizedContext = context.createConfigurationContext(config)
        dateFormatter = DateFormatter(localizedContext)
    }

    @Test
    fun `Test 'now' formatting`() {
        val calendar = Calendar.getInstance()
        val formattedDate = dateFormatter.getConditionallyRelativeFormattedTimeSpan(calendar)
        val expected = context.getString(R.string.date_formatting_now)
        assertEquals(expected, formattedDate)
    }

    @Test
    fun `Test relative minutes formatting`() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, -5)
        val formattedDate = dateFormatter.getConditionallyRelativeFormattedTimeSpan(calendar)
        val expected = context.getString(R.string.date_formatting_relative_minutes, 5)
        assertEquals(expected, formattedDate)
    }

    @Test
    fun `Test relative hours formatting`() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR, -3)
        val formattedDate = dateFormatter.getConditionallyRelativeFormattedTimeSpan(calendar)
        val expected = context.resources.getQuantityString(R.plurals.date_formatting_relative_hours, 3, 3)
        assertEquals(expected, formattedDate)
    }

    @Test
    fun `Test day of the week formatting`() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -4)
        val sdf = SimpleDateFormat("EEE", Locale.US)
        val expected = sdf.format(calendar.time)
        val formattedDate = dateFormatter.getConditionallyRelativeFormattedTimeSpan(calendar)
        assertEquals(expected, formattedDate)
    }

    @Test
    fun `Test month and day formatting`() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -100)
        val sdf = SimpleDateFormat("MMM d", Locale.US)
        val expected = sdf.format(calendar.time)
        val formattedDate = dateFormatter.getConditionallyRelativeFormattedTimeSpan(calendar)
        assertEquals(expected, formattedDate)
    }

    @Test
    fun `Test full date formatting`() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -2)
        val sdf = SimpleDateFormat("MMM d, yyyy", Locale.US)
        val expected = sdf.format(calendar.time)
        val formattedDate = dateFormatter.getConditionallyRelativeFormattedTimeSpan(calendar)
        assertEquals(expected, formattedDate)
    }
}
