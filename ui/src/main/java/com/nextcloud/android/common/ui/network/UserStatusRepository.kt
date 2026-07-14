/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.ui.network

import com.nextcloud.android.common.ui.network.http.HttpMethod
import com.nextcloud.android.common.ui.network.http.NextcloudHttpClient
import com.nextcloud.android.common.ui.network.model.NetworkResult
import com.nextcloud.android.common.ui.network.model.OcsResponse
import com.nextcloud.android.common.ui.network.serialization.OCSSerializer

class UserStatusRepository(private val client: NextcloudHttpClient) {

    private companion object {
        private const val PREDEFINED_STATUSES_ENDPOINT =
            "/ocs/v2.php/apps/user_status/api/v1/predefined_statuses"
    }

    suspend fun fetchPredefinedStatuses(): NetworkResult<List<PredefinedStatus>> =
        client.executeRequest(
            endpoint = PREDEFINED_STATUSES_ENDPOINT,
            method = HttpMethod.GET
        ) { body ->
            OCSSerializer.json.decodeFromString<OcsResponse<List<PredefinedStatus>>>(body).ocs.data
        }
}
