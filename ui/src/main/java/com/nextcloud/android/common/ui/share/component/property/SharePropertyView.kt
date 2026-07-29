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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val propertyErrors by viewModel.propertyErrors.collectAsStateWithLifecycle()
    val fallbackError = stringResource(R.string.share_view_update_error_message)
    val errorMessage = if (propertyErrors.containsKey(property.clazz)) {
        propertyErrors[property.clazz] ?: fallbackError
    } else {
        null
    }

    Column {
        SharePropertyField(
            shareId = shareId,
            property = property,
            isError = errorMessage != null,
            viewModel = viewModel
        )

        SharePropertyMessage(errorMessage = errorMessage, hint = property.hint)
    }
}

@Composable
private fun SharePropertyField(
    shareId: String,
    property: Property,
    isError: Boolean,
    viewModel: ShareViewModel
) {
    when (property) {
        is PropertyBoolean -> {
            var checkedValue by remember(property.clazz) { mutableStateOf(property.isTrue()) }

            ShareSwitch(
                label = property.displayName,
                checked = checkedValue,
                onCheckedChange = { isChecked ->
                    checkedValue = isChecked
                    viewModel.updateProperty(shareId, property.clazz, isChecked.toString())
                }
            )
        }

        is PropertyString -> ShareTextPropertyField(
            shareId = shareId,
            clazz = property.clazz,
            displayName = property.displayName,
            initialValue = property.value ?: "",
            isError = isError,
            viewModel = viewModel
        )

        is PropertyPassword -> ShareTextPropertyField(
            shareId = shareId,
            clazz = property.clazz,
            displayName = property.displayName,
            initialValue = property.value ?: "",
            isError = isError,
            viewModel = viewModel,
            visualTransformation = PasswordVisualTransformation()
        )

        is PropertyDate -> ShareDatePicker(
            property = property,
            isError = isError,
            onDateSelected = { dateValue -> viewModel.updateProperty(shareId, property.clazz, dateValue) }
        )

        is PropertyEnum -> SharePropertyEnumField(
            property = property,
            isError = isError,
            onValueSelected = { value -> viewModel.updateProperty(shareId, property.clazz, value) }
        )
    }
}

@Composable
private fun ShareTextPropertyField(
    shareId: String,
    clazz: String,
    displayName: String,
    initialValue: String,
    isError: Boolean,
    viewModel: ShareViewModel,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    var value by remember(clazz) { mutableStateOf(initialValue) }
    var committedValue by remember(clazz) { mutableStateOf(initialValue) }
    var wasFocused by remember(clazz) { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = {
            value = it
            viewModel.onPropertyEdited(clazz, it != committedValue)
        },
        label = { Text(displayName) },
        visualTransformation = visualTransformation,
        isError = isError,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .onFocusChanged { focusState ->
                if (wasFocused && !focusState.isFocused && value != committedValue) {
                    committedValue = value
                    viewModel.updateProperty(shareId, clazz, value)
                }
                wasFocused = focusState.isFocused
            },
        singleLine = true
    )
}
