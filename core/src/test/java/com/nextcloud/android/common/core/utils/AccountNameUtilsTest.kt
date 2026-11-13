/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Alper Ozturk <alper.ozturk@nextcloud.com>
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.core.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AccountNameUtilsTest {
    private val accountNames =
        mapOf(
            "user@cloud.example.com" to ("user" to "cloud.example.com"),
            "John Doe@cloud.example.com" to ("John Doe" to "cloud.example.com"),
            "user'name@cloud.example.com" to ("user'name" to "cloud.example.com"),
            "john.doe@example.com@cloud.example.com" to ("john.doe@example.com" to "cloud.example.com"),
            "a@b@c@cloud.example.com" to ("a@b@c" to "cloud.example.com"),
            "CORP\\user@cloud.example.com" to ("CORP\\user" to "cloud.example.com"),
            "ünïcode@cloud.example.com" to ("ünïcode" to "cloud.example.com"),
            "user@cloud.example.com:8443/nextcloud" to ("user" to "cloud.example.com:8443/nextcloud"),
            "user@[2001:db8::1]:8443" to ("user" to "[2001:db8::1]:8443"),
            "user@localhost" to ("user" to "localhost")
        )

    @Test
    fun `user and server parts are split at the last separator`() {
        accountNames.forEach { (accountName, parts) ->
            assertEquals(accountName, parts.first, AccountNameUtils.getUserPart(accountName))
            assertEquals(accountName, parts.second, AccountNameUtils.getServerPart(accountName))
        }
    }

    @Test
    fun `build is reversed by user and server parts`() {
        accountNames.values.forEach { (user, server) ->
            val accountName = AccountNameUtils.build(user, server)
            assertEquals(user, AccountNameUtils.getUserPart(accountName))
            assertEquals(server, AccountNameUtils.getServerPart(accountName))
        }
    }

    @Test
    fun `account name without separator has no server part`() {
        assertEquals("user", AccountNameUtils.getUserPart("user"))
        assertEquals("", AccountNameUtils.getServerPart("user"))
        assertEquals("", AccountNameUtils.getServerPart("user@"))
    }

    @Test
    fun `scheme is removed from server urls`() {
        assertEquals(
            "cloud.example.com/nextcloud",
            AccountNameUtils.removeScheme("https://cloud.example.com/nextcloud")
        )
        assertEquals("cloud.example.com:8080", AccountNameUtils.removeScheme("http://cloud.example.com:8080"))
        assertEquals("cloud.example.com", AccountNameUtils.removeScheme("cloud.example.com"))
    }

    @Test
    fun `equivalent servers are the same server`() {
        listOf(
            "https://server.net" to "https://server.net",
            "https://server.net/" to "https://server.net",
            "https://server.net/index.php" to "https://server.net",
            "https://server.net/nextcloud/" to "https://server.net/nextcloud/index.php",
            "https://Server.NET" to "https://server.net",
            "https://server.net:443" to "https://server.net",
            "http://server.net:80" to "http://server.net",
            "http://server.net" to "https://server.net",
            "http://server.net:443" to "https://server.net",
            "https://server.net:8443" to "https://server.net:8443",
            "https://my_host.local" to "https://My_Host.local",
            "https://[2001:DB8::1]:8443/nc" to "https://[2001:db8::1]:8443/nc",
            "https://bücher.example" to "https://xn--bcher-kva.example",
            "https://cloud.example.com." to "https://cloud.example.com",
            "http://10.0.2.2:8080" to "http://10.0.2.2:8080",
            "https://localhost" to "https://localhost",
            "cloud.example.com:8443/nextcloud" to "https://cloud.example.com:8443/nextcloud/",
            "Cloud.Example.com" to "https://cloud.example.com"
        ).forEach { (first, second) ->
            assertTrue("$first vs $second", AccountNameUtils.isSameServer(first, second))
        }
    }

    @Test
    fun `different servers are not the same server`() {
        listOf(
            "https://server.net" to "https://other.net",
            "https://server.net" to "https://sub.server.net",
            "https://server.net:8443" to "https://server.net",
            "https://server.net:80" to "https://server.net",
            "https://server.net/nextcloud" to "https://server.net",
            "https://server.net/Nextcloud" to "https://server.net/nextcloud",
            "https://[2001:db8::1]:8443" to "https://[2001:db8::1:8443]",
            "https://[2001:db8::1]" to "https://[2001:db8::2]",
            "cloud.example.com" to "cloud.example.com:8443"
        ).forEach { (first, second) ->
            assertFalse("$first vs $second", AccountNameUtils.isSameServer(first, second))
        }
    }

    @Test
    fun `same user id on the same server is the same account`() {
        assertTrue(AccountNameUtils.isSameAccount("https://cloud.example.com", "user", "cloud.example.com/", "User"))
        assertTrue(
            AccountNameUtils.isSameAccount(
                "https://cloud.example.com/nextcloud",
                "john.doe@example.com",
                "https://Cloud.Example.com/nextcloud/",
                "john.doe@example.com"
            )
        )
    }

    @Test
    fun `different user id or server is not the same account`() {
        assertFalse(
            AccountNameUtils.isSameAccount("https://cloud.example.com", "user", "https://cloud.example.com", "other")
        )
        assertFalse(
            AccountNameUtils.isSameAccount("https://cloud.example.com", "user", "https://other.example.com", "user")
        )
        assertFalse(
            AccountNameUtils.isSameAccount("https://cloud.example.com", "user", "https://cloud.example.com/nc", "user")
        )
    }

    @Test
    fun `account names in user at server format are valid`() {
        listOf(
            "abc@example.cloud.com",
            "user.name+test@sub.domain.org",
            "user_123@test.co",
            "abc@example",
            "abc@example.",
            "abc@example.c",
            "abc example@test.com",
            "o'brien@cloud.example.com",
            "john doe@cloud.example.com",
            "-user@cloud.example.com",
            ".user@cloud.example.com",
            "_@cloud.example.com",
            "a@cloud.example.com",
            "user+tag!#$%&*@cloud.example.com",
            "domain\\user@cloud.example.com",
            "日本語@cloud.example.com",
            "user@localhost",
            "user@localhost:8080",
            "user@my_host.local",
            "user@[::1]",
            "user@[::1]:8443/nextcloud",
            "user@bücher.example",
            "user@xn--bcher-kva.example",
            "user@cloud.example.com.:443",
            "user@cloud.example.com/",
            "user@cloud.example.com/nextcloud",
            "user@cloud.example.com/my%20cloud",
            "user@cloud.example.com/@nextcloud",
            "abc@cloud.com@example.inst2.cloud.com",
            "admin@10.0.2.2:55002",
            "admin@10.0.2.2",
            "test@example.tld@cloud.example.tld:443/subfolder/deep"
        ).forEach {
            assertTrue(it, AccountNameUtils.isValid(it))
        }
    }

    @Test
    fun `malformed account names are not valid`() {
        listOf(
            "",
            "  ",
            "abc",
            "abc@",
            "abc@.com",
            "@example.com",
            " user@cloud.example.com",
            "user@cloud.example.com ",
            "user\n@cloud.example.com",
            "user@cloud..example.com",
            "user@cloud.example.com:123456",
            "user@cloud.example.com:",
            "user@cloud.example.com?x=1",
            "user@cloud.example.com#fragment",
            "user@cloud.example.com/my cloud",
            "user@[]"
        ).forEach {
            assertFalse(it, AccountNameUtils.isValid(it))
        }
    }
}
