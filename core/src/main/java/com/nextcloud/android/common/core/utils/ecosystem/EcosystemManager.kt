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
class EcosystemManager(
    private val activity: Activity
) {
    companion object {
        private const val TAG = "EcosystemManager"

        private const val ECOSYSTEM_INTENT_ACTION = "com.nextcloud.intent.OPEN_ECOSYSTEM_APP"
        private const val PLAY_STORE_LINK = "https://play.google.com/store/apps/details?id="
        private const val PLAY_STORE_MARKET_LINK = "market://details?id="
        private const val EXTRA_KEY_ACCOUNT = "KEY_ACCOUNT"

        const val ACCOUNT_NAME_PATTERN_REGEX = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,}"
    }

    private val accountNamePattern = Pattern.compile(ACCOUNT_NAME_PATTERN_REGEX)

    /**
     * Opens an ecosystem app with the given account information.
     *
     * If the target app is installed, it will be launched using the
     * {@link #ECOSYSTEM_INTENT_ACTION} intent action and the account name
     * will be passed as an intent extra.
     *
     * If the app is not installed or cannot be launched, the user will be
     * redirected to the Google Play Store page for the app.
     *
     * @param app The ecosystem app to be opened (e.g. Notes, Files, Talk)
     * @param accountName The account name associated with the user,
     *        e.g. "abc@example.cloud.com"
     */
    @Suppress("TooGenericExceptionCaught", "ReturnCount")
    fun openApp(
        app: EcosystemApp,
        accountName: String?
    ) {
        Log.d(TAG, "open app, package name: ${app.packageNames}, account name: $accountName")

        // check account name emptiness
        if (accountName.isNullOrBlank()) {
            Log.w(TAG, "given account name is null")
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
        val intent = activity.findLaunchIntentForInstalledPackage(app.packageNames)
        if (intent == null) {
            Log.w(TAG, "given package name cannot be found")
            showSnackbar(R.string.ecosystem_app_not_found)
            openAppInStore(app)
            return
        }

        try {
            Log.d(TAG, "launching app ${app.name} with account=$accountName")
            val launchIntent =
                Intent(ECOSYSTEM_INTENT_ACTION).apply {
                    setPackage(intent.`package`)
                    putExtra(EXTRA_KEY_ACCOUNT, accountName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
            activity.startActivity(launchIntent)
        } catch (e: Exception) {
            showSnackbar(R.string.ecosystem_store_open_failed)
            Log.e(TAG, "exception launching app ${app.packageNames}: $e")
        }
    }

    /**
     * Finds the first launchable intent from a list of package names.
     *
     * @return launch Intent or null if none of the apps are installed
     */
    private fun Context.findLaunchIntentForInstalledPackage(packageNames: List<String>): Intent? {
        val pm: PackageManager = packageManager
        return packageNames.firstNotNullOfOrNull { packageName ->
            pm.getLaunchIntentForPackage(packageName)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun openAppInStore(app: EcosystemApp) {
        Log.d(TAG, "open app in store: $app")

        val firstPackageName = app.packageNames.firstOrNull() ?: return

        val marketUri = "$PLAY_STORE_MARKET_LINK$firstPackageName".toUri()
        val intent = Intent(Intent.ACTION_VIEW, marketUri)

        try {
            activity.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            val playStoreUri = "$PLAY_STORE_LINK$firstPackageName".toUri()
            val webIntent = Intent(Intent.ACTION_VIEW, playStoreUri)
            try {
                activity.startActivity(webIntent)
            } catch (e: Exception) {
                showSnackbar(R.string.ecosystem_store_open_failed)
                Log.e(TAG, "No browser available to open store for $firstPackageName, exception: ", e)
            }
        }
    }

    /**
     * Receives account from an intent and triggers the callback.
     *
     * This method should be called from your Activity's `onCreate()` and `onNewIntent()`
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
    @Suppress("ReturnCount")
    fun receiveAccount(
        intent: Intent?,
        callback: AccountReceiverCallback
    ) {
        Log.d(TAG, "receive account started")

        if (intent == null) {
            Log.d(TAG, "received intent is null")
            val message = activity.getString(R.string.ecosystem_wrong_intent)
            callback.onAccountError(message)
            return
        }

        if (intent.action != ECOSYSTEM_INTENT_ACTION) {
            Log.d(TAG, "received intent action is not matching")
            val message = activity.getString(R.string.ecosystem_wrong_intent)
            callback.onAccountError(message)
            return
        }

        val account = intent.getStringExtra(EXTRA_KEY_ACCOUNT)

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

        Log.d(TAG, "Account received from intent: $account")
        callback.onAccountReceived(account)
    }

    private fun showSnackbar(messageRes: Int) {
        val rootContent = activity.findViewById<View>(android.R.id.content)
        Snackbar.make(rootContent, activity.getString(messageRes), Snackbar.LENGTH_LONG).show()
    }
}
