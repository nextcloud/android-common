/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.ui

import androidx.annotation.PluralsRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.share.model.api.recipients.Recipient

private val SUMMARY_PLURALS =
    mapOf(
        Recipient.USER_RECIPIENT_CLASS to R.plurals.share_view_recipient_summary_people,
        Recipient.GROUP_RECIPIENT_CLASS to R.plurals.share_view_recipient_summary_groups,
        Recipient.TEAM_RECIPIENT_CLASS to R.plurals.share_view_recipient_summary_teams,
        Recipient.EMAIL_RECIPIENT_CLASS to R.plurals.share_view_recipient_summary_emails,
        Recipient.TOKEN_RECIPIENT_CLASS to R.plurals.share_view_recipient_summary_links
    )

@Composable
fun recipientSummary(recipients: List<Recipient>): String {
    val counts = recipients.groupingBy { it.clazz }.eachCount()
    val parts =
        SUMMARY_PLURALS.mapNotNull { (clazz, pluralId) ->
            counts[clazz]?.let { count -> quantityLabel(pluralId, count) }
        }
    val unknownCount = recipients.count { it.clazz !in SUMMARY_PLURALS }
    val unknownPart = quantityLabel(R.plurals.share_view_recipient_summary_others, unknownCount)

    return (parts + listOfNotNull(unknownPart)).joinToString(
        separator = stringResource(R.string.share_view_recipient_summary_separator)
    )
}

@Composable
private fun quantityLabel(@PluralsRes pluralId: Int, count: Int): String? {
    if (count == 0) return null
    return pluralStringResource(pluralId, count, count)
}
