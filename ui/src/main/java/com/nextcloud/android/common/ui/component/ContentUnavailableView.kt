/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

@file:Suppress("FunctionNaming", "UnusedPrivateMember")

package com.nextcloud.android.common.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.nextcloud.android.common.ui.R

private val CONTENT_PADDING = 24.dp
private val ICON_SIZE = 64.dp
private val ICON_TITLE_SPACING = 16.dp
private val TITLE_DESCRIPTION_SPACING = 4.dp

@Composable
fun ContentUnavailableView(title: String, description: String? = null, iconId: Int? = null) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(CONTENT_PADDING),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        iconId?.let {
            Image(
                painter = painterResource(iconId),
                modifier = Modifier.size(ICON_SIZE),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(ICON_TITLE_SPACING))
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        description?.let {
            Spacer(modifier = Modifier.height(TITLE_DESCRIPTION_SPACING))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ContentUnavailableViewPreview() {
    MaterialTheme {
        ContentUnavailableView(
            iconId = R.drawable.ic_error,
            title = stringResource(R.string.share_view_fetch_error_message),
            description = stringResource(R.string.share_view_fetch_error_description)
        )
    }
}
