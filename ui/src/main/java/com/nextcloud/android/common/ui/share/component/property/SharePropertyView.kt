/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.component.property

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.nextcloud.android.common.ui.share.ShareViewModel
import com.nextcloud.android.common.ui.share.component.property.datepicker.ShareDatePicker
import com.nextcloud.android.common.ui.share.component.ShareSwitch
import com.nextcloud.android.common.ui.share.model.api.property.Property
import com.nextcloud.android.common.ui.share.model.api.property.PropertyBoolean
import com.nextcloud.android.common.ui.share.model.api.property.PropertyDate
import com.nextcloud.android.common.ui.share.model.api.property.PropertyEnum
import com.nextcloud.android.common.ui.share.model.api.property.PropertyPassword
import com.nextcloud.android.common.ui.share.model.api.property.PropertyString
import kotlinx.coroutines.delay

@Composable
fun SharePropertyView(shareId: String, property: Property, viewModel: ShareViewModel) {
    when (property) {
        is PropertyBoolean -> {
            var checkedValue by remember(property.clazz) { mutableStateOf(property.isTrue()) }

            ShareSwitch(
                label = property.displayName,
                checked = checkedValue,
                onCheckedChange = { isChecked ->
                    checkedValue = isChecked
                    viewModel.updatePropertyLocally(property.clazz, isChecked.toString())
                }
            )
        }

        is PropertyString -> {
            var textValue by remember(property.clazz) { mutableStateOf(property.value ?: "") }

            DebouncedPropertyUpdater(
                value = textValue,
                originalValue = property.value ?: "",
                onDebounceComplete = { viewModel.updatePropertyLocally(property.clazz, it) }
            )

            OutlinedTextField(
                value = textValue,
                onValueChange = { textValue = it },
                label = { Text(property.displayName) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                singleLine = true
            )
        }

        is PropertyPassword -> {
            var passwordValue by remember(property.clazz) { mutableStateOf(property.value ?: "") }

            DebouncedPropertyUpdater(
                value = passwordValue,
                originalValue = property.value ?: "",
                onDebounceComplete = { viewModel.updatePropertyLocally(property.clazz, it) }
            )

            OutlinedTextField(
                value = passwordValue,
                onValueChange = { passwordValue = it },
                label = { Text(property.displayName) },
                placeholder = property.hint?.let { { Text(it) } },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                singleLine = true
            )
        }

        is PropertyDate -> {
            ShareDatePicker(property, onDateSelected = { dateValue ->
                viewModel.updateProperty(shareId, property.clazz, dateValue)
            })
        }

        is PropertyEnum -> {
            // TODO: Implement ExposedDropdownMenuBox using property.validValues
            Text(text = "Enum Property: ${property.displayName} (Under Construction)", color = Color.Gray)
        }
    }
}

@Composable
private fun DebouncedPropertyUpdater(
    value: String,
    originalValue: String,
    delayMillis: Long = 400L,
    onDebounceComplete: (String) -> Unit
) {
    LaunchedEffect(value) {
        if (value != originalValue) {
            delay(delayMillis)
            onDebounceComplete(value)
        }
    }
}

