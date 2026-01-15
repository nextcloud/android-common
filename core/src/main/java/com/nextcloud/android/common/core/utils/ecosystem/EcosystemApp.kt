/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.core.utils.ecosystem

/**
 * Represents Nextcloud ecosystem apps that can communicate with each other.
 *
 * Each enum value corresponds to a specific app in the Nextcloud ecosystem,
 * and holds a list of package names for that app. Multiple package names
 * allow compatibility with different flavours (Play Store, F-Droid, QA, beta/dev versions, etc.).
 *
 */
enum class EcosystemApp(
    val packageNames: List<String>
) {
    FILES(
        listOf(
            "com.nextcloud.client", // generic, gplay, huawei
            "com.nextcloud.android.beta", // versionDev
            "com.nextcloud.android.qa" // qa
        )
    ),
    NOTES(
        listOf(
            "it.niedermann.owncloud.notes", // play, fdroid
            "it.niedermann.owncloud.notes.dev", // dev
            "it.niedermann.owncloud.notes.qa" // qa
        )
    ),
    TALK(
        listOf(
            "com.nextcloud.talk2", // generic, gplay
            "com.nextcloud.talk2.qa" // qa
        )
    )
}
