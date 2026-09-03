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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.share.model.api.recipients.Recipient
import kotlinx.coroutines.launch

private const val MAX_TOKEN_LENGTH = 32

@Composable
fun CustomLink(recipient: Recipient, onGenerateSecret: suspend () -> String?, onTokenChange: (String) -> Unit) {
    val prefix = remember(recipient.secret.url, recipient.secret.value) {
        val url = recipient.secret.url.orEmpty()
        val token = recipient.secret.value.orEmpty()
        if (token.isNotEmpty() && url.endsWith(token)) url.removeSuffix(token) else url
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
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

        CustomLinkTokenField(
            initialToken = recipient.secret.value ?: "",
            prefix = prefix,
            onGenerateSecret = onGenerateSecret,
            onCommit = onTokenChange
        )
    }
}

@Composable
private fun CustomLinkTokenField(
    initialToken: String,
    prefix: String,
    onGenerateSecret: suspend () -> String?,
    onCommit: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var token by remember(initialToken) { mutableStateOf(initialToken) }
    var committedToken by remember(initialToken) { mutableStateOf(initialToken) }
    var wasFocused by remember(initialToken) { mutableStateOf(false) }

    fun commit() {
        if (token == committedToken || token.isBlank()) return
        committedToken = token
        onCommit(token)
    }

    OutlinedTextField(
        value = token,
        onValueChange = { newValue -> token = newValue.take(MAX_TOKEN_LENGTH) },
        label = { Text(prefix) },
        singleLine = true,
        trailingIcon = {
            IconButton(
                onClick = {
                    scope.launch {
                        val generated = onGenerateSecret() ?: return@launch
                        token = generated
                        committedToken = generated
                        onCommit(generated)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.share_view_custom_link_refresh)
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (wasFocused && !focusState.isFocused) commit()
                wasFocused = focusState.isFocused
            }
    )
}
