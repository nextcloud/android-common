/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.component.property.datepicker.util

import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.getSelectedDate
import com.nextcloud.android.common.ui.share.model.api.property.PropertyDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

class ShareDateFormatter {

    fun getDisplayName(property: PropertyDate): String = property.displayName

    @OptIn(ExperimentalMaterial3Api::class)
    fun formatDisplayDate(datePickerState: DatePickerState): String? =
        datePickerState.getSelectedDate()?.format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.getDefault())
        )

    @OptIn(ExperimentalMaterial3Api::class)
    fun formatIso8601Date(datePickerState: DatePickerState): String? =
        datePickerState.getSelectedDate()
            ?.atStartOfDay(ZoneId.systemDefault())
            ?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}
