/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.share.model.api.recipients.Recipient
import java.util.UUID

@Composable
fun CustomLink(recipient: Recipient, onTokenChange: (String) -> Unit) {
    val prefix = remember(recipient.secret.url, recipient.secret.value) {
        val url = recipient.secret.url.orEmpty()
        val token = recipient.secret.value.orEmpty()
        if (token.isNotEmpty() && url.endsWith(token)) url.removeSuffix(token) else url
    }
    var token by remember(recipient.value) { mutableStateOf(recipient.secret.value ?: "") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.share_view_custom_link_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = stringResource(R.string.share_view_custom_link_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
        )

        OutlinedTextField(
            value = token,
            onValueChange = {
                token = it
                onTokenChange(it)
            },
            label = {
                Text(prefix)
            },
            singleLine = true,
            trailingIcon = {
                IconButton(
                    onClick = {
                        val refreshedToken = UUID.randomUUID().toString()
                        token = refreshedToken
                        onTokenChange(refreshedToken)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.share_view_custom_link_refresh)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
