/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.core.utils.ecosystem

enum class EcosystemApp(val packageNames: List<String>) {
    FILES(
        listOf(
            "com.nextcloud.client",        // generic, gplay, huawei
            "com.nextcloud.android.beta",  // versionDev
            "com.nextcloud.android.qa"     // qa
        )
    ),
    NOTES(
        listOf(
            "it.niedermann.owncloud.notes",      // play, fdroid
            "it.niedermann.owncloud.notes.dev",  // dev
            "it.niedermann.owncloud.notes.qa"    // qa
        )
    ),
    TALK(
        listOf(
            "com.nextcloud.talk2",     // generic, gplay
            "com.nextcloud.talk2.qa"   // qa
        )
    )
}
