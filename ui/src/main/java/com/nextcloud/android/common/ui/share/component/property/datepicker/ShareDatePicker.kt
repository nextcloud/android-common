/*
 * Nextcloud - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Alper Ozturk <alper.ozturk@nextcloud.com>
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */

package com.nextcloud.android.common.ui.share.component.property.datepicker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.share.component.property.datepicker.util.ShareDateFormatter
import com.nextcloud.android.common.ui.share.model.api.property.PropertyDate

@Composable
fun ShareDatePicker(property: PropertyDate, onDateSelected: (String) -> Unit) {
    val formatter = ShareDateFormatter()
    var showDatePicker by remember { mutableStateOf(false) }
    var dateValue by remember { mutableStateOf(property.value ?: "") }


    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = dateValue,
            onValueChange = {},
            readOnly = true,
            enabled = true,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(formatter.getDisplayName(property)) },
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_calendar),
                    contentDescription = null
                )
            }
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showDatePicker = true }
        )
    }

    if (showDatePicker) {
        DatePickerModal(formatter, onDateSelected = {
            dateValue = it ?: ""
            onDateSelected(dateValue)
        }, onDismiss = {
            showDatePicker = false
        })
    }
}

@Composable
private fun DatePickerModal(
    formatter: ShareDateFormatter,
    onDateSelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(formatter.formatDate(datePickerState))
                onDismiss()
            }) {
                Text(stringResource(R.string.common_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.common_cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Preview(showBackground = true)
@Composable
private fun ShareDatePickerPreview() {
    MaterialTheme {
        ShareDatePicker(
            property = PropertyDate(
                clazz = "expiry",
                displayName = "Expiration Date",
                priority = 1,
                required = false,
                value = "2026-12-31",
                minDate = "2026-01-01",
                maxDate = "2027-12-31"
            ),
            onDateSelected = {}
        )
    }
}
