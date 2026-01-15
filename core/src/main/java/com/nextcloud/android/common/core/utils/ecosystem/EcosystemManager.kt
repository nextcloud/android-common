/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

package com.nextcloud.android.common.core.utils.ecosystem

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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

    private val ecoSystemIntentAction = "com.nextcloud.intent.OPEN_ECOSYSTEM_APP"

    private val accountNamePattern = Pattern.compile(
        "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,}"
    )

    fun openApp(app: EcosystemApp, accountName: String?) {
        Log.d(tag, "open app, package name: ${app.packageNames}, account name: $accountName")

        // check account name emptiness
        if (accountName.isNullOrBlank()) {
            Log.w(tag, "given account name is null")
            showSnackbar(R.string.ecosystem_null_account)
            openAppInStore(app)
            return
        }

        // validate account name
        if (!accountNamePattern.matcher(accountName).matches()) {
            showSnackbar(R.string.ecosystem_invalid_account)
            return
        }

        // validate package name
        val intent = activity.getLaunchIntentForPackages(app.packageNames)
        if (intent == null) {
            Log.w(tag, "given package name cannot be found")
            showSnackbar(R.string.ecosystem_app_not_found)
            openAppInStore(app)
            return
        }

        try {
            Log.d(tag, "launching app ${app.name} with account=$accountName")
            val launchIntent = Intent(ecoSystemIntentAction).apply {
                setPackage(intent.`package`)
                putExtra(keyAccount, accountName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            activity.startActivity(launchIntent)
        } catch (e: Exception) {
            showSnackbar(R.string.ecosystem_store_open_failed)
            Log.e(tag, "exception launching app ${app.packageNames}: $e")
        }
    }


    /**
     * Finds the first launchable intent from a list of package names.
     *
     * @return launch Intent or null if none of the apps are installed
     */
    private fun Context.getLaunchIntentForPackages(
        packageNames: List<String>
    ): Intent? {
        val pm: PackageManager = packageManager
        return packageNames.firstNotNullOfOrNull { packageName ->
            pm.getLaunchIntentForPackage(packageName)
        }
    }

    private fun openAppInStore(app: EcosystemApp) {
        Log.d(tag, "open app in store: $app")

        val firstPackageName = app.packageNames.firstOrNull() ?: return

        val intent = Intent(Intent.ACTION_VIEW, "market://details?id=${firstPackageName}".toUri())

        try {
            activity.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                "https://play.google.com/store/apps/details?id=${firstPackageName}".toUri()
            )

            try {
                activity.startActivity(webIntent)
            } catch (e: Exception) {
                showSnackbar(R.string.ecosystem_store_open_failed)
                Log.e(tag, "No browser available to open store for ${firstPackageName}, exception: ", e)
            }
        }
    }

    /**
     * Receives account from an intent and triggers the callback.
     *
     * This method should be called from your Activity's `onNewIntent()`
     * to handle incoming ecosystem intents from other ecosystem apps.
     *
     * Important:
     * 1. The sending app calls openApp.
     * 2. The receiving app must declare an intent-filter in its manifest to handle this action:
     *
     * <activity android:name=".ui.activity.MainActivity"
     *           android:exported="true"
     *           android:launchMode="singleTop">
     *     <intent-filter>
     *         <action android:name="com.nextcloud.intent.OPEN_ECOSYSTEM_APP" />
     *         <category android:name="android.intent.category.DEFAULT" />
     *     </intent-filter>
     * </activity>
     *
     */
    fun receiveAccount(intent: Intent?, callback: AccountReceiverCallback) {
        Log.d(tag, "receive account started")

        if (intent == null) {
            Log.d(tag, "received intent is null")
            val message = activity.getString(R.string.ecosystem_null_intent)
            callback.onAccountError(message)
            return
        }

        if (intent.action != ecoSystemIntentAction) {
            Log.d(tag, "received intent action is not matching")
            val message = activity.getString(R.string.ecosystem_received_intent_action_not_matching)
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
