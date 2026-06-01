/*
 * Nextcloud - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Alper Ozturk <alper.ozturk@nextcloud.com>
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */

package com.nextcloud.android.common.ui.share.component.property

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun SharePropertyView(shareId: String, property: Property, viewModel: ShareViewModel) {
    when (property) {
        is PropertyBoolean -> {
            ShareSwitch(
                label = property.displayName,
                checked = property.value == "true",
                onCheckedChange = { isChecked ->
                    viewModel.updateProperty(shareId, property.clazz, isChecked.toString())
                }
            )
        }

        is PropertyString -> {
            OutlinedTextField(
                value = property.value ?: "",
                onValueChange = { viewModel.updateProperty(shareId, property.clazz, it) },
                label = { Text(property.displayName) },
                placeholder = property.hint?.let { { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                singleLine = true
            )
        }

        is PropertyPassword -> {
            OutlinedTextField(
                value = property.value ?: "",
                onValueChange = { viewModel.updateProperty(shareId, property.clazz, it) },
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
