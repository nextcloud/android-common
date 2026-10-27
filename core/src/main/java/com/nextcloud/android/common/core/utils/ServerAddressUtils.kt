/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Alper Ozturk <alper.ozturk@nextcloud.com>
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.core.utils

import java.net.IDN
import java.util.Locale

internal object ServerAddressUtils {
    private const val SCHEME_SEPARATOR = "://"
    private const val PATH_SEPARATOR = '/'
    private const val PORT_SEPARATOR = ':'
    private const val HOST_LABEL_SEPARATOR = '.'
    private const val QUERY_START = '?'
    private const val FRAGMENT_START = '#'
    private const val IPV6_START = '['
    private const val IPV6_END = ']'
    private const val INDEX_PHP_PATH = "/index.php"
    private const val HTTP_SCHEME = "http"
    private const val HTTP_PORT = "80"
    private const val HTTPS_PORT = "443"
    private const val MAX_PORT_LENGTH = 5

    fun removeScheme(serverUrl: String): String = serverUrl.substringAfter(SCHEME_SEPARATOR)

    fun isValid(server: String): Boolean {
        if (server.isEmpty() || server.any { it.isForbiddenInServer() }) {
            return false
        }

        val authority = server.substringBefore(PATH_SEPARATOR)
        val host = authority.toHost()
        val portPart = authority.removePrefix(host)
        val port = portPart.removePrefix(PORT_SEPARATOR.toString())
        val isValidPort =
            portPart.isEmpty() ||
                (portPart.startsWith(PORT_SEPARATOR) && port.length in 1..MAX_PORT_LENGTH && port.all(Char::isDigit))
        val isValidHost =
            if (host.startsWith(IPV6_START)) {
                host.removeSurrounding(IPV6_START.toString(), IPV6_END.toString()).isNotEmpty()
            } else {
                host.trimEnd(HOST_LABEL_SEPARATOR).split(HOST_LABEL_SEPARATOR).none { it.isEmpty() }
            }

        return isValidHost && isValidPort
    }

    fun toComparable(server: String): String {
        val scheme = server.substringBefore(SCHEME_SEPARATOR, "").lowercase(Locale.ROOT)
        val withoutScheme = removeScheme(server)
        val authority = withoutScheme.substringBefore(PATH_SEPARATOR)
        val path =
            withoutScheme
                .removePrefix(authority)
                .trimEnd(PATH_SEPARATOR)
                .removeSuffix(INDEX_PHP_PATH)
                .trimEnd(PATH_SEPARATOR)

        val host = authority.toHost()
        val isIpv6 = host.startsWith(IPV6_START)
        val port = authority.removePrefix(host).removePrefix(PORT_SEPARATOR.toString())
        val defaultPort = if (scheme == HTTP_SCHEME) HTTP_PORT else HTTPS_PORT
        val effectivePort = port.takeUnless { it.isEmpty() || it == defaultPort } ?: HTTPS_PORT
        val comparablePort = if (effectivePort == HTTPS_PORT) "" else "$PORT_SEPARATOR$effectivePort"
        val comparableHost = if (isIpv6) host.lowercase(Locale.ROOT) else host.toComparableHostName()

        return comparableHost + comparablePort + path
    }

    private fun String.toHost(): String = if (contains(IPV6_END)) {
        substringBefore(IPV6_END) + IPV6_END
    } else {
        substringBefore(PORT_SEPARATOR)
    }

    private fun Char.isForbiddenInServer(): Boolean = isWhitespace() || this == QUERY_START || this == FRAGMENT_START

    private fun String.toComparableHostName(): String {
        val host = lowercase(Locale.ROOT).trimEnd(HOST_LABEL_SEPARATOR)
        return runCatching { IDN.toASCII(host, IDN.ALLOW_UNASSIGNED) }.getOrDefault(host)
    }
}
