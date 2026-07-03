/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.component.property

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nextcloud.android.common.ui.share.model.api.property.PropertyEnum

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharePropertyEnumField(
    property: PropertyEnum,
    errorMessage: String?,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedValue by remember(property.clazz) { mutableStateOf(property.value ?: "") }

    ExposedDropdownMenuBox(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(property.displayName) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            isError = errorMessage != null,
            supportingText = errorMessage?.let { { Text(it) } },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            property.validValues.forEach { value ->
                DropdownMenuItem(
                    text = { Text(value) },
                    onClick = {
                        selectedValue = value
                        expanded = false
                        onValueSelected(value)
                    }
                )
            }
        }
    }
}
