/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.network

import com.nextcloud.android.common.ui.network.api.ApiHttpClient
import com.nextcloud.android.common.ui.network.model.ApiResult
import com.nextcloud.android.common.ui.network.model.Meta
import com.nextcloud.android.common.ui.network.model.Ocs
import com.nextcloud.android.common.ui.network.model.OcsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.Request

class UserStatusService(private val client: ApiHttpClient) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun fetchPredefinedStatuses(): ApiResult<List<PredefinedStatus>> =
        withContext(Dispatchers.IO) {
            val url =
                client.credentials.baseURL.trimEnd('/') +
                    "/ocs/v2.php/apps/user_status/api/v1/predefined_statuses"

            val request =
                Request
                    .Builder()
                    .url(url)
                    .header("Accept", "application/json")
                    .build()

            try {
                val response = client.okHttpClient.newCall(request).execute()
                val body = response.body.string()

                if (response.isSuccessful) {
                    val parsed = json.decodeFromString<OcsResponse<List<PredefinedStatus>>>(body)
                    ApiResult.Success(parsed.ocs.data)
                } else {
                    val error = json.decodeFromString<OcsResponse<String>>(body)
                    ApiResult.Error(error)
                }
            } catch (e: Exception) {
                ApiResult.Error(
                    OcsResponse(
                        Ocs(
                            meta =
                                Meta(
                                    status = "error",
                                    statusCode = -1,
                                    message = e.message ?: "Unknown error",
                                    totalItems = "",
                                    itemsPerPage = ""
                                ),
                            data = e.message ?: "Unknown error"
                        )
                    )
                )
            }
        }
}
