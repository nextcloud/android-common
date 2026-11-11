/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Alper Ozturk <alper.ozturk@nextcloud.com>
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.core.network

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

object LocalNetworkPermission {
    // Android 17 only blocks the local network for apps that target Android 17 or higher
    @JvmStatic
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.CINNAMON_BUN)
    fun isRequired(context: Context): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.CINNAMON_BUN &&
        context.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.CINNAMON_BUN

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.CINNAMON_BUN)
    fun isGranted(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_LOCAL_NETWORK) ==
            PackageManager.PERMISSION_GRANTED
}
