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
import java.time.format.DateTimeFormatter

class ShareDateFormatter {
    companion object {
        private const val PATTERN = "MM-dd-yyyy"
    }

    fun getDisplayName(property: PropertyDate): String {
        return property.displayName + " ($PATTERN)"
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun formatDate(datePickerState: DatePickerState): String? {
        val date = datePickerState.getSelectedDate()
        val formatter = DateTimeFormatter.ofPattern(PATTERN)
        return date?.format(formatter)
    }
}