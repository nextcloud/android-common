/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.component

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.share.model.api.recipients.Recipient

@Composable
fun RecipientTokenField(recipient: Recipient, onTokenChange: (String) -> Unit) {
    var token by remember(recipient.value) { mutableStateOf(recipient.secret.value ?: "") }

    OutlinedTextField(
        value = token,
        onValueChange = {
            token = it
            onTokenChange(it)
        },
        label = { Text(stringResource(R.string.share_view_link_token_label)) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    )
}
