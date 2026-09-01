/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.share.model.api

import kotlinx.serialization.serializer

inline fun <reified T : Enum<T>> T.apiValue(): String = serializer<T>().descriptor.getElementName(ordinal)
