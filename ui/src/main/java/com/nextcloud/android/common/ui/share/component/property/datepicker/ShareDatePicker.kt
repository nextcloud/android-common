/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.component.property.datepicker

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.share.component.property.datepicker.util.NoPastSelectableDates
import com.nextcloud.android.common.ui.share.component.property.datepicker.util.ShareDateFormatter
import com.nextcloud.android.common.ui.share.model.api.property.PropertyDate
import com.nextcloud.android.common.ui.util.extensions.Strings

@Composable
fun ShareDatePicker(property: PropertyDate, isError: Boolean, onDateSelected: (String) -> Unit) {
    val formatter = remember { ShareDateFormatter() }
    var showDatePicker by remember { mutableStateOf(false) }
    var dateValue by remember(property.clazz) { mutableStateOf(formatter.formatIsoForDisplay(property.value)) }

    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                showDatePicker = true
            }
        }
    }

    OutlinedTextField(
        value = dateValue,
        onValueChange = {},
        readOnly = true,
        enabled = true,
        isError = isError,
        modifier = Modifier.fillMaxWidth(),
        interactionSource = interactionSource,
        label = { Text(formatter.getDisplayName(property)) },
        trailingIcon = {
            val isDateBlank = dateValue.isBlank()
            val iconId = if (isDateBlank) {
                R.drawable.ic_calendar
            } else {
                R.drawable.ic_cancel
            }
            IconButton(onClick = {
                if (isDateBlank) {
                    showDatePicker = true
                } else {
                    dateValue = Strings.EMPTY
                    onDateSelected(Strings.EMPTY)
                }
            }) {
                Icon(
                    painter = painterResource(iconId),
                    contentDescription = null
                )
            }
        }
    )

    if (showDatePicker) {
        DatePickerModal(
            formatter = formatter,
            onDateSelected = { displayDate, isoDate ->
                dateValue = displayDate ?: Strings.EMPTY
                onDateSelected(isoDate ?: Strings.EMPTY)
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
private fun DatePickerModal(
    formatter: ShareDateFormatter,
    onDateSelected: (displayDate: String?, isoDate: String?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        selectableDates = remember { NoPastSelectableDates() }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(
                    formatter.formatDisplayDate(datePickerState),
                    formatter.formatIso8601Date(datePickerState)
                )
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
                advanced = false,
                value = "2026-12-31",
                minDate = "2026-01-01",
                maxDate = "2027-12-31"
            ),
            isError = false,
            onDateSelected = {}
        )
    }
}
