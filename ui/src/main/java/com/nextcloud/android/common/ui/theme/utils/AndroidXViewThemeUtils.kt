/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2022-2023 Nextcloud GmbH and Nextcloud contributors
 * SPDX-FileCopyrightText: 2022-2023 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-FileCopyrightText: 2023 Thore Goebel <goebel.thore@gmail.com>
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.nextcloud.android.common.ui.theme.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.preference.PreferenceCategory
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.NotificationCompat
import androidx.core.widget.TextViewCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.theme.MaterialSchemes
import com.nextcloud.android.common.ui.theme.ViewThemeUtilsBase
import javax.inject.Inject

/**
 * View theme utils for Android extension views (androidx.*)
 */
class AndroidXViewThemeUtils
    @Inject
    constructor(
        schemes: MaterialSchemes,
        private val androidViewThemeUtils: AndroidViewThemeUtils
    ) :
    ViewThemeUtilsBase(schemes) {
        fun colorSwitchCompat(switchCompat: SwitchCompat) {
            withScheme(switchCompat) { scheme ->
                val colors = SwitchColorUtils.calculateSwitchColors(switchCompat.context, scheme)
                switchCompat.thumbTintList = colors.thumbColor
                switchCompat.trackTintList = colors.trackColor
            }
        }

        fun themeSwipeRefreshLayout(swipeRefreshLayout: SwipeRefreshLayout) {
            withScheme(swipeRefreshLayout) { scheme ->
                swipeRefreshLayout.setColorSchemeColors(scheme.primary)
                swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.refresh_spinner_background)
            }
        }

        fun colorPrimaryTextViewElement(textView: AppCompatTextView) {
            withScheme(textView) { scheme ->
                textView.setTextColor(scheme.primary)
                TextViewCompat.setCompoundDrawableTintList(textView, ColorStateList.valueOf(scheme.primary))
            }
        }

        // TODO host the back arrow in this lib instead of passing it everywhere
        fun themeActionBar(
            context: Context,
            actionBar: ActionBar,
            title: String,
            backArrow: Drawable
        ) {
            withScheme(context) { scheme ->
                val text: Spannable = getColoredSpan(title, scheme.onSurface)
                actionBar.title = text
                themeActionBar(context, actionBar, backArrow)
            }
        }

        fun themeActionBar(
            context: Context,
            actionBar: ActionBar,
            backArrow: Drawable
        ) {
            withScheme(context) { scheme ->
                actionBar.setBackgroundDrawable(ColorDrawable(scheme.surface))
                val indicator = androidViewThemeUtils.colorDrawable(backArrow, scheme.onSurface)
                actionBar.setHomeAsUpIndicator(indicator)
            }
        }

        fun themeActionBarSubtitle(
            context: Context,
            actionBar: ActionBar
        ) {
            withScheme(context) { scheme ->
                actionBar.subtitle = getColoredSpan(actionBar.subtitle.toString(), scheme.onSurfaceVariant)
            }
        }

        fun themeToolbarSearchView(searchView: SearchView) {
            withScheme(searchView) { scheme ->
                // hacky as no default way is provided
                val editText =
                    searchView.findViewById<AppCompatAutoCompleteTextView>(androidx.appcompat.R.id.search_src_text)
                val searchPlate = searchView.findViewById<LinearLayout>(androidx.appcompat.R.id.search_plate)
                val closeButton = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
                val searchButton = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
                editText.setHintTextColor(scheme.onSurfaceVariant)
                editText.highlightColor = scheme.inverseOnSurface
                editText.setTextColor(scheme.onSurface)
                closeButton.setColorFilter(scheme.onSurface)
                searchButton.setColorFilter(scheme.onSurface)
                searchPlate.setBackgroundColor(scheme.surface)
            }
        }

        fun themeNotificationCompatBuilder(
            context: Context,
            builder: NotificationCompat.Builder
        ) {
            withScheme(context) { scheme ->
                builder.setColor(scheme.primary)
            }
        }

        fun themePreferenceCategory(category: PreferenceCategory) {
            withScheme(category.context) { scheme ->
                val text: Spannable = SpannableString(category.title)
                text.setSpan(
                    ForegroundColorSpan(scheme.primary),
                    0,
                    text.length,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                category.title = text
            }
        }

        private fun getColoredSpan(
            title: String,
            color: Int
        ): Spannable {
            val text: Spannable = SpannableString(title)
            text.setSpan(
                ForegroundColorSpan(color),
                0,
                text.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            return text
        }
    }
