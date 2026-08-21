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
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.share.component.ShareSwitch
import com.nextcloud.android.common.ui.share.component.property.datepicker.ShareDatePicker
import com.nextcloud.android.common.ui.share.model.api.property.Property
import com.nextcloud.android.common.ui.share.model.api.property.PropertyBoolean
import com.nextcloud.android.common.ui.share.model.api.property.PropertyDate
import com.nextcloud.android.common.ui.share.model.api.property.PropertyEnum
import com.nextcloud.android.common.ui.share.model.api.property.PropertyPassword
import com.nextcloud.android.common.ui.share.model.api.property.PropertyString

private const val SINGLE_LINE_COUNT = 1
private const val MULTILINE_MIN_LINES = 1
private const val MULTILINE_MAX_LINES = 3

@Composable
fun propertyErrorMessage(propertyErrors: Map<String, String?>, clazz: String): String? {
    val fallbackError = stringResource(R.string.share_view_update_error_message)
    if (!propertyErrors.containsKey(clazz)) return null
    return propertyErrors[clazz] ?: fallbackError
}

@Composable
fun SharePropertyView(
    property: Property,
    errorMessage: String?,
    onValueChange: (String?) -> Unit
) {
    Column {
        SharePropertyField(
            property = property,
            isError = errorMessage != null,
            onValueChange = onValueChange
        )

        SharePropertyMessage(errorMessage = errorMessage, hint = property.hint)
    }
}

@Composable
private fun SharePropertyField(
    property: Property,
    isError: Boolean,
    onValueChange: (String?) -> Unit
) {
    when (property) {
        is PropertyBoolean -> {
            var checkedValue by remember(property.clazz) { mutableStateOf(property.isTrue()) }

            ShareSwitch(
                label = property.displayName,
                checked = checkedValue,
                onCheckedChange = { isChecked ->
                    checkedValue = isChecked
                    onValueChange(isChecked.toString())
                }
            )
        }

        is PropertyString -> ShareTextPropertyField(
            property = property,
            isError = isError,
            onValueChange = onValueChange
        )

        is PropertyPassword -> ShareTextPropertyField(
            property = property,
            isError = isError,
            onValueChange = onValueChange,
            visualTransformation = PasswordVisualTransformation()
        )

        is PropertyDate -> ShareDatePicker(
            property = property,
            isError = isError,
            onDateSelected = onValueChange
        )

        is PropertyEnum -> SharePropertyEnumField(
            property = property,
            isError = isError,
            onValueSelected = onValueChange
        )
    }
}

@Composable
private fun ShareTextPropertyField(
    property: Property,
    isError: Boolean,
    onValueChange: (String?) -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val stringProperty = property as? PropertyString
    val isMultiline = stringProperty?.isMultiline == true
    val maxLength = stringProperty?.maxLength
    val clazz = property.clazz
    val initialValue = property.value ?: ""
    var value by remember(clazz) { mutableStateOf(initialValue) }
    var committedValue by remember(clazz) { mutableStateOf(initialValue) }
    var wasFocused by remember(clazz) { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (maxLength == null || newValue.length <= maxLength) {
                value = newValue
            }
        },
        label = { Text(property.displayName) },
        visualTransformation = visualTransformation,
        isError = isError,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .onFocusChanged { focusState ->
                if (wasFocused && !focusState.isFocused && value != committedValue) {
                    committedValue = value
                    onValueChange(value)
                }
                wasFocused = focusState.isFocused
            },
        singleLine = !isMultiline,
        minLines = if (isMultiline) MULTILINE_MIN_LINES else SINGLE_LINE_COUNT,
        maxLines = if (isMultiline) MULTILINE_MAX_LINES else SINGLE_LINE_COUNT
    )
}
