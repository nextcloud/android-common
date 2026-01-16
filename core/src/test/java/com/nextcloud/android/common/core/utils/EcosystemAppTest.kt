/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.core.utils

import com.nextcloud.android.common.core.utils.ecosystem.EcosystemApp
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for {@link EcosystemApp}.
 *
 * These tests ensure that the order of package names is preserved and that
 * the first entry for each ecosystem app always represents the production
 * package name.
 *
 * The order is important because the first package name is used when
 * redirecting users to the Play Store or resolving fallback behavior.
 */
class EcosystemAppTest {
    @Test
    fun `first package name must always be the production package`() {
        assertEquals(
            "com.nextcloud.client",
            EcosystemApp.FILES.packageNames.first()
        )

        assertEquals(
            "it.niedermann.owncloud.notes",
            EcosystemApp.NOTES.packageNames.first()
        )

        assertEquals(
            "com.nextcloud.talk2",
            EcosystemApp.TALK.packageNames.first()
        )
    }
}
