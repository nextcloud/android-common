/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.network.model

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()

    data class ServerError(val response: OcsResponse<String>) : NetworkResult<Nothing>()

    data class NetworkException(val throwable: Throwable) : NetworkResult<Nothing>()

    val isSuccess: Boolean get() = this is Success

    companion object {
        fun fromException(e: Throwable): NetworkException = NetworkException(e)
    }
}

inline fun <T> NetworkResult<T>.dataOrElse(onError: () -> Unit): T? =
    when (this) {
        is NetworkResult.Success -> data
        is NetworkResult.ServerError,
        is NetworkResult.NetworkException -> {
            onError()
            null
        }
    }
