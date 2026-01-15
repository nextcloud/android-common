/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.core.utils.ecosystem

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import com.google.android.material.snackbar.Snackbar
import com.nextcloud.android.common.core.R
import java.util.regex.Pattern

/**
 * EcosystemManager: handles sending and receiving account info across apps
 * within the Nextcloud ecosystem (Notes, Files, Talk).
 *
 * Supports:
 *  - Opening apps with account info
 *  - Redirecting to Play Store if app not installed
 *  - Receiving account info from intents with callback support
 */
class EcosystemManager(private val activity: Activity) {

    private val tag = "EcosystemManager"

    /**
     * Key used to pass account name e.g. abc@example.cloud.com
     */
    private val keyAccount = "KEY_ACCOUNT"

    private val accountNamePattern = Pattern.compile(
        "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,}"
    )

    fun openApp(appPackage: EcosystemAppPackage, accountName: String?) {
        Log.d(tag, "open app, package name: ${appPackage.name}, account name: $accountName")

        // check account name emptiness
        if (accountName.isNullOrBlank()) {
            Log.w(tag, "given account name is null")
            showSnackbar(R.string.ecosystem_null_account)
            openAppInStore(appPackage)
            return
        }

        // validate account name
        if (!accountNamePattern.matcher(accountName).matches()) {
            showSnackbar(R.string.ecosystem_invalid_account)
            return
        }

        // validate package name
        val intent = activity.packageManager.getLaunchIntentForPackage(appPackage.name)
        if (intent == null) {
            Log.w(tag, "given package name cannot be found")
            showSnackbar(R.string.ecosystem_app_not_found)
            openAppInStore(appPackage)
            return
        }

        try {
            Log.d(tag, "launching app ${appPackage.name} with userHash=$accountName")
            intent.putExtra(keyAccount, accountName)
            activity.startActivity(intent)
        } catch (e: Exception) {
            showSnackbar(R.string.ecosystem_store_open_failed)
            Log.e(tag, "exception launching app ${appPackage.name}: $e")
        }
    }

    private fun openAppInStore(appPackage: EcosystemAppPackage) {
        Log.d(tag, "open app in store: $appPackage")

        val intent = Intent(Intent.ACTION_VIEW, "market://details?id=${appPackage.name}".toUri())

        try {
            activity.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                "https://play.google.com/store/apps/details?id=${appPackage.name}".toUri()
            )

            try {
                activity.startActivity(webIntent)
            } catch (e: Exception) {
                showSnackbar(R.string.ecosystem_store_open_failed)
                Log.e(tag, "No browser available to open store for ${appPackage.name}, exception: ", e)
            }
        }
    }

    /**
     * Receives account info from an intent and triggers the callback.
     *
     * @param intent The Intent received in onCreate() or onNewIntent()
     * @param callback Callback to notify the client about success or failure
     *
     * Usage example in Activity:
     *
     * override fun onCreate(savedInstanceState: Bundle?) {
     *     super.onCreate(savedInstanceState)
     *     setContentView(R.layout.activity_main)
     *
     *     EcosystemManager(rootView = findViewById(R.id.root_layout))
     *         .receiveAccount(intent, object : EcosystemManager.AccountReceiverCallback {
     *             override fun onAccountReceived(accountName: String) {
     *                 // Use accountName for login or other actions
     *                 Log.d("Receiver", "Received account: $accountName")
     *             }
     *
     *             override fun onAccountError(reason: String) {
     *                 // Show error or fallback
     *                 Log.w("Receiver", "Error receiving account: $reason")
     *             }
     *         })
     * }
     */
    fun receiveAccount(intent: Intent?, callback: AccountReceiverCallback) {
        if (intent == null) {
            Log.d(tag, "received intent is null")
            val message = activity.getString(R.string.ecosystem_null_intent)
            callback.onAccountError(message)
            return
        }

        val account = intent.getStringExtra(keyAccount)

        if (account.isNullOrBlank()) {
            val message = activity.getString(R.string.ecosystem_null_account)
            callback.onAccountError(message)
            return
        }

        if (!accountNamePattern.matcher(account).matches()) {
            val message = activity.getString(R.string.ecosystem_received_account_invalid)
            callback.onAccountError(message)
            return
        }

        Log.d(tag, "Account received from intent: $account")
        callback.onAccountReceived(account)
    }

    private fun showSnackbar(messageRes: Int) {
        val rootContent = activity.findViewById<View>(android.R.id.content)
        Snackbar.make(rootContent, activity.getString(messageRes), Snackbar.LENGTH_LONG).show()
    }
}
