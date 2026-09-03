/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api.recipients

import com.nextcloud.android.common.ui.share.model.api.icon.Icon
import com.nextcloud.android.common.ui.share.model.api.permission.Permission
import com.nextcloud.android.common.ui.share.model.api.secret.Secret
import com.nextcloud.android.common.ui.share.model.api.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Recipient(
    @SerialName("class")
    val clazz: String,
    val value: String,
    val instance: String? = null,
    @SerialName("display_name")
    val displayName: String,
    val icon: Icon? = null,
    val secret: Secret,
    val initiator: User? = null,
    val permissions: List<Permission> = emptyList()
) {
    companion object {
        const val USER_RECIPIENT_CLASS = """OC\Core\Sharing\Recipient\UserShareRecipientType"""
        const val GROUP_RECIPIENT_CLASS = """OC\Core\Sharing\Recipient\GroupShareRecipientType"""
        const val TEAM_RECIPIENT_CLASS = """OC\Core\Sharing\Recipient\TeamShareRecipientType"""
        const val EMAIL_RECIPIENT_CLASS = """OC\Core\Sharing\Recipient\EmailShareRecipientType"""
        const val TOKEN_RECIPIENT_CLASS = """OC\Core\Sharing\Recipient\TokenShareRecipientType"""
    }

    fun isSameAs(other: Recipient): Boolean = clazz == other.clazz && value == other.value && instance == other.instance
}
