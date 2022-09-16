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
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.TextViewCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.theme.MaterialSchemes
import com.nextcloud.android.common.ui.theme.ViewThemeUtilsBase
import javax.inject.Inject

/**
 * View theme utils for Android extension views (androidx.*)
 */
// TODO breaking change: androidViewThemeUtils parameter
class AndroidXViewThemeUtils @Inject constructor(
    schemes: MaterialSchemes,
    private val androidViewThemeUtils: AndroidViewThemeUtils
) :
    ViewThemeUtilsBase(schemes) {

    fun colorSwitchCompat(switchCompat: SwitchCompat) {
        withScheme(switchCompat) { scheme ->

            val context = switchCompat.context

            val thumbUncheckedColor = ResourcesCompat.getColor(
                context.resources,
                R.color.switch_thumb_color_unchecked,
                context.theme
            )
            val trackUncheckedColor = ResourcesCompat.getColor(
                context.resources,
                R.color.switch_track_color_unchecked,
                context.theme
            )

            val trackColor = Color.argb(
                SWITCH_COMPAT_TRACK_ALPHA,
                Color.red(scheme.primary),
                Color.green(scheme.primary),
                Color.blue(scheme.primary)
            )

            switchCompat.thumbTintList = ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                intArrayOf(scheme.primary, thumbUncheckedColor)
            )

            switchCompat.trackTintList = ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                intArrayOf(trackColor, trackUncheckedColor)
            )
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
    fun themeActionBar(context: Context, actionBar: ActionBar, title: String, backArrow: Drawable) {
        withScheme(context) { scheme ->
            setColoredTitle(actionBar, title, scheme.onSurface)
            themeActionBar(context, actionBar, backArrow)
        }
    }

    fun themeActionBar(context: Context, actionBar: ActionBar, backArrow: Drawable) {
        withScheme(context) { scheme ->
            actionBar.setBackgroundDrawable(ColorDrawable(scheme.surface))
            val indicator = androidViewThemeUtils.tintDrawable(backArrow, scheme.onSurface)
            actionBar.setHomeAsUpIndicator(indicator)
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
            editText.setHintTextColor(scheme.onSurfaceVariant)
            editText.highlightColor = scheme.inverseOnSurface
            editText.setTextColor(scheme.onSurface)
            closeButton.setColorFilter(scheme.onSurface)
            searchButton.setColorFilter(scheme.onSurface)
            searchPlate.setBackgroundColor(scheme.surface)
        }
    }

    private fun setColoredTitle(
        actionBar: ActionBar,
        title: String,
        @ColorInt color: Int
    ) {
        val text: Spannable = SpannableString(title)
        text.setSpan(
            ForegroundColorSpan(color),
            0,
            text.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        actionBar.title = text
    }

    companion object {
        private const val SWITCH_COMPAT_TRACK_ALPHA: Int = 77
    }
}
