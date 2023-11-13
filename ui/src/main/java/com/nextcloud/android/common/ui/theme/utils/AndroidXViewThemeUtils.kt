/*
 * Nextcloud Android Common Library
 *
 * @author Álvaro Brey
 * @author Andy Scherzinger
 * Copyright (C) 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * Copyright (C) 2022 Andy Scherzinger <info@andy-scherzinger.de>
 * Copyright (C) 2022 Nextcloud GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.nextcloud.android.common.ui.theme.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.NotificationCompat
import androidx.core.widget.TextViewCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.theme.MaterialSchemes
import com.nextcloud.android.common.ui.theme.ViewThemeUtilsBase
import dynamiccolor.MaterialDynamicColors
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
        private val dynamicColor = MaterialDynamicColors()
        
        fun colorSwitchCompat(switchCompat: SwitchCompat) {
            withScheme(switchCompat) { scheme ->
                val colors = SwitchColorUtils.calculateSwitchColors(switchCompat.context, scheme)
                switchCompat.thumbTintList = colors.thumbColor
                switchCompat.trackTintList = colors.trackColor
            }
        }

        fun themeSwipeRefreshLayout(swipeRefreshLayout: SwipeRefreshLayout) {
            withScheme(swipeRefreshLayout) { scheme ->
                swipeRefreshLayout.setColorSchemeColors(dynamicColor.primary().getArgb(scheme))
                swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.refresh_spinner_background)
            }
        }

        fun colorPrimaryTextViewElement(textView: AppCompatTextView) {
            withScheme(textView) { scheme ->
                textView.setTextColor(dynamicColor.primary().getArgb(scheme))
                TextViewCompat.setCompoundDrawableTintList(textView, ColorStateList.valueOf(dynamicColor.primary().getArgb(scheme)))
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
                val text: Spannable = getColoredSpan(title, dynamicColor.onSurface().getArgb(scheme))
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
                actionBar.setBackgroundDrawable(ColorDrawable(dynamicColor.surface().getArgb(scheme)))
                val indicator = androidViewThemeUtils.colorDrawable(backArrow, dynamicColor.onSurface().getArgb(scheme))
                actionBar.setHomeAsUpIndicator(indicator)
            }
        }

        fun themeActionBarSubtitle(
            context: Context,
            actionBar: ActionBar
        ) {
            withScheme(context) { scheme ->
                actionBar.subtitle = getColoredSpan(actionBar.subtitle.toString(), dynamicColor.onSurfaceVariant().getArgb(scheme))
            }
        }

        fun themeToolbarSearchView(searchView: SearchView) {
            withScheme(searchView) { scheme ->
                // hacky as no default way is provided
                val editText =
                    searchView.findViewById<SearchView.SearchAutoComplete>(androidx.appcompat.R.id.search_src_text)
                val searchPlate = searchView.findViewById<LinearLayout>(androidx.appcompat.R.id.search_plate)
                val closeButton = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
                val searchButton = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
                editText.setHintTextColor(dynamicColor.onSurfaceVariant().getArgb(scheme))
                editText.highlightColor = dynamicColor.inverseOnSurface().getArgb(scheme)
                editText.setTextColor(dynamicColor.onSurface().getArgb(scheme))
                closeButton.setColorFilter(dynamicColor.onSurface().getArgb(scheme))
                searchButton.setColorFilter(dynamicColor.onSurface().getArgb(scheme))
                searchPlate.setBackgroundColor(dynamicColor.surface().getArgb(scheme))
            }
        }

        fun themeNotificationCompatBuilder(
            context: Context,
            builder: NotificationCompat.Builder
        ) {
            withScheme(context) { scheme ->
                builder.setColor(dynamicColor.primary().getArgb(scheme))
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
