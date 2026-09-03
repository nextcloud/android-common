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

class NoPastSelectableDates : SelectableDates {
    private val today: LocalDate = LocalDate.now(ZoneOffset.UTC)
    private val todayUtcMillis: Long = today.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis >= todayUtcMillis

    override fun isSelectableYear(year: Int): Boolean = year >= today.year
}
