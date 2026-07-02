/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api.recipients

import com.nextcloud.android.common.ui.share.model.api.icon.Icon
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

    val initiator: User? = null
) {
    companion object {
        const val TOKEN_RECIPIENT_CLASS = "OC\\CoreSharing\\Recipient\\TokenShareRecipientType"
    }
}
