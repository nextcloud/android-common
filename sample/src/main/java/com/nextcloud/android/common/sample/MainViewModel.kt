/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2022-2023 Nextcloud GmbH and Nextcloud contributors
 * SPDX-FileCopyrightText: 2023 Stefan Niedermann <info@niedermann.it>
 * SPDX-License-Identifier: MIT
 */
package com.nextcloud.android.common.sample

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextcloud.android.common.ui.network.api.ApiCredentials
import com.nextcloud.android.common.ui.network.model.ApiResult
import com.nextcloud.android.common.ui.network.api.ApiHttpClient
import com.nextcloud.android.common.ui.network.UserStatusService
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val color = MutableLiveData<Int>()
    val apiTestResult = MutableLiveData<String>()

    fun testPredefinedStatuses(
        baseUrl: String,
        username: String,
        token: String
    ) {
        viewModelScope.launch {
            val credentials = ApiCredentials(baseUrl, username, token)
            val client = ApiHttpClient.create(credentials, enableLogging = true)
            val service = UserStatusService(client)

            when (val result = service.fetchPredefinedStatuses()) {
                is ApiResult.Success ->
                    apiTestResult.value =
                        "✅ Success (${result.data.size} statuses):\n" +
                            result.data.joinToString("\n") { "${it.icon} ${it.message}" }

                is ApiResult.Error ->
                    apiTestResult.value =
                        "❌ Error ${result.error.ocs.meta.statusCode}: ${result.error.ocs.meta.message}"
            }
        }
    }
}