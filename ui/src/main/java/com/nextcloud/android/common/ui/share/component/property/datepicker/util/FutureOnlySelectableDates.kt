/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.component.property.datepicker.util

import androidx.compose.material3.SelectableDates
import java.time.LocalDate
import java.time.ZoneOffset

class FutureOnlySelectableDates : SelectableDates {
    private val tomorrow: LocalDate = LocalDate.now(ZoneOffset.UTC).plusDays(1)
    private val tomorrowUtcMillis: Long = tomorrow.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis >= tomorrowUtcMillis

    override fun isSelectableYear(year: Int): Boolean = year >= tomorrow.year
}
