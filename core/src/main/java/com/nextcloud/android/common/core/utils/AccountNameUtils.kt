/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Alper Ozturk <alper.ozturk@nextcloud.com>
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.core.utils

object AccountNameUtils {
    private const val SEPARATOR = '@'

    @JvmStatic
    fun build(user: String, server: String): String = "$user$SEPARATOR$server"

    @JvmStatic
    fun getUserPart(accountName: String): String = accountName.substringBeforeLast(SEPARATOR)

    @JvmStatic
    fun getServerPart(accountName: String): String = accountName.substringAfterLast(SEPARATOR, "")

    @JvmStatic
    fun removeScheme(serverUrl: String): String = ServerAddressUtils.removeScheme(serverUrl)

    @JvmStatic
    fun isSameServer(firstServer: String, secondServer: String): Boolean =
        ServerAddressUtils.toComparable(firstServer) == ServerAddressUtils.toComparable(secondServer)

    @JvmStatic
    fun isSameAccount(firstServer: String, firstUserId: String, secondServer: String, secondUserId: String): Boolean =
        isSameServer(firstServer, secondServer) && firstUserId.equals(secondUserId, ignoreCase = true)

    @JvmStatic
    fun isValid(accountName: String): Boolean {
        val user = getUserPart(accountName)
        val isValidUser = user.isNotBlank() && user.trim() == user
        return isValidUser && ServerAddressUtils.isValid(getServerPart(accountName))
    }
}
