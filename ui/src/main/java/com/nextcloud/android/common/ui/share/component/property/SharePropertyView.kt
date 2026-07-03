/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.component.property

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.share.ShareViewModel
import com.nextcloud.android.common.ui.share.component.ShareSwitch
import com.nextcloud.android.common.ui.share.component.property.datepicker.ShareDatePicker
import com.nextcloud.android.common.ui.share.model.api.property.Property
import com.nextcloud.android.common.ui.share.model.api.property.PropertyBoolean
import com.nextcloud.android.common.ui.share.model.api.property.PropertyDate
import com.nextcloud.android.common.ui.share.model.api.property.PropertyEnum
import com.nextcloud.android.common.ui.share.model.api.property.PropertyPassword
import com.nextcloud.android.common.ui.share.model.api.property.PropertyString

@Composable
fun SharePropertyView(shareId: String, property: Property, viewModel: ShareViewModel) {
    val propertyErrors by viewModel.propertyErrors.collectAsState()
    val fallbackError = stringResource(R.string.share_view_update_error_message)
    val errorMessage = if (propertyErrors.containsKey(property.clazz)) {
        propertyErrors[property.clazz] ?: fallbackError
    } else {
        null
    }

    when (property) {
        is PropertyBoolean -> {
            var checkedValue by remember(property.clazz) { mutableStateOf(property.isTrue()) }

            Column {
                ShareSwitch(
                    label = property.displayName,
                    checked = checkedValue,
                    onCheckedChange = { isChecked ->
                        checkedValue = isChecked
                        viewModel.updateProperty(shareId, property.clazz, isChecked.toString())
                    }
                )
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                }
            }
        }

        is PropertyString -> {
            var textValue by remember(property.clazz) { mutableStateOf(property.value ?: "") }

            OutlinedTextField(
                value = textValue,
                onValueChange = {
                    textValue = it
                    viewModel.updateProperty(shareId, property.clazz, it)
                },
                label = { Text(property.displayName) },
                isError = errorMessage != null,
                supportingText = errorMessage?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                singleLine = true
            )
        }

        is PropertyPassword -> {
            var passwordValue by remember(property.clazz) { mutableStateOf(property.value ?: "") }

            OutlinedTextField(
                value = passwordValue,
                onValueChange = {
                    passwordValue = it
                    viewModel.updateProperty(shareId, property.clazz, it)
                },
                label = { Text(property.displayName) },
                placeholder = property.hint?.let { { Text(it) } },
                visualTransformation = PasswordVisualTransformation(),
                isError = errorMessage != null,
                supportingText = errorMessage?.let { { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                singleLine = true
            )
        }

        is PropertyDate -> {
            ShareDatePicker(property, errorMessage = errorMessage, onDateSelected = { dateValue ->
                viewModel.updateProperty(shareId, property.clazz, dateValue)
            })
        }

        is PropertyEnum -> {
            SharePropertyEnumField(
                property = property,
                errorMessage = errorMessage,
                onValueSelected = { value -> viewModel.updateProperty(shareId, property.clazz, value) }
            )
        }
    }
}
