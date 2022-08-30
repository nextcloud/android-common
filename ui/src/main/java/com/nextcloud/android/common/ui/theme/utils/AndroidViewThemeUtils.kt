/*
 * Nextcloud Android Common Library
 *
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

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.color.ColorUtil
import com.nextcloud.android.common.ui.theme.MaterialSchemes
import com.nextcloud.android.common.ui.theme.ViewThemeUtilsBase
import com.nextcloud.android.common.ui.util.PlatformThemeUtil
import javax.inject.Inject

/**
 * View theme utils for platform views (android.widget.*, android.view.*)
 */
@Suppress("TooManyFunctions")
class AndroidViewThemeUtils @Inject constructor(schemes: MaterialSchemes, private val colorUtil: ColorUtil) :
    ViewThemeUtilsBase(schemes) {

    fun colorViewBackground(view: View) {
        withScheme(view) { scheme ->
            view.setBackgroundColor(scheme.surface)
        }
    }

    fun colorToolbarMenuIcon(context: Context, item: MenuItem) {
        withScheme(context) { scheme ->
            item.icon.setColorFilter(scheme.onSurface, PorterDuff.Mode.SRC_ATOP)
        }
    }

    fun themeStatusBar(activity: Activity, view: View) {
        withScheme(view) { scheme ->
            applyColorToStatusBar(activity, scheme.surface)
        }
    }

    private fun applyColorToStatusBar(activity: Activity, @ColorInt color: Int) {
        val window = activity.window
        val isLightTheme = !PlatformThemeUtil.isDarkMode(activity)
        if (window != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val decor = window.decorView
                if (isLightTheme) {
                    val systemUiFlagLightStatusBar = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                            View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    } else {
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    }
                    decor.systemUiVisibility = systemUiFlagLightStatusBar
                } else {
                    decor.systemUiVisibility = 0
                }
                window.statusBarColor = color
            } else if (isLightTheme) {
                window.statusBarColor = Color.BLACK
            }
        }
    }

    fun resetStatusBar(activity: Activity) {
        applyColorToStatusBar(
            activity,
            ResourcesCompat.getColor(
                activity.resources,
                R.color.bg_default,
                activity.theme
            )
        )
    }

    fun themeDialog(view: View) {
        withScheme(view) { scheme ->
            view.setBackgroundColor(scheme.surface)
        }
    }

    fun themeDialogDark(view: View) {
        withSchemeDark { scheme ->
            view.setBackgroundColor(scheme.surface)
        }
    }

    fun themeDialogDivider(view: View) {
        withScheme(view) { scheme ->
            view.setBackgroundColor(scheme.surfaceVariant)
        }
    }

    fun themeHorizontalSeekBar(seekBar: SeekBar) {
        withScheme(seekBar) { scheme ->
            themeHorizontalProgressBar(seekBar, scheme.primary)
            seekBar.thumb.setColorFilter(scheme.primary, PorterDuff.Mode.SRC_IN)
        }
    }

    fun themeHorizontalProgressBar(progressBar: ProgressBar?, @ColorInt color: Int) {
        if (progressBar != null) {
            progressBar.indeterminateDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            progressBar.progressDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
    }

    fun colorPrimaryTextViewElement(textView: TextView) {
        withScheme(textView) { scheme ->
            textView.setTextColor(scheme.primary)
        }
    }

    fun colorPrimaryTextViewElementDarkMode(textView: TextView) {
        withSchemeDark { scheme ->
            textView.setTextColor(scheme.primary)
        }
    }

    fun colorPrimaryView(view: View) {
        withScheme(view) { scheme ->
            view.setBackgroundColor(scheme.primary)
        }
    }

    /**
     * Colors the background as element color and the foreground as text color.
     */
    fun colorImageViewButton(imageView: ImageView) {
        withScheme(imageView) { scheme ->
            imageView.imageTintList = ColorStateList.valueOf(scheme.onPrimaryContainer)
            imageView.backgroundTintList = ColorStateList.valueOf(scheme.primaryContainer)
        }
    }

    fun themeImageButton(imageButton: ImageButton) {
        withScheme(imageButton) { scheme ->
            imageButton.imageTintList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_selected),
                    intArrayOf(-android.R.attr.state_selected),
                    intArrayOf(android.R.attr.state_enabled),
                    intArrayOf(-android.R.attr.state_enabled)
                ),
                intArrayOf(
                    scheme.primary,
                    scheme.onSurfaceVariant,
                    scheme.onSurfaceVariant,
                    colorUtil.adjustOpacity(scheme.onSurface, ON_SURFACE_OPACITY_BUTTON_DISABLED)
                )
            )
        }
    }

    /**
     * Tints the image with element color
     */
    fun colorImageView(imageView: ImageView) {
        withScheme(imageView) { scheme ->
            imageView.imageTintList = ColorStateList.valueOf(scheme.primary)
        }
    }

    fun colorTextButtons(vararg buttons: Button) {
        withScheme(buttons[0]) { scheme ->
            for (button in buttons) {
                button.setTextColor(
                    ColorStateList(
                        arrayOf(
                            intArrayOf(android.R.attr.state_enabled),
                            intArrayOf(-android.R.attr.state_enabled)
                        ),
                        intArrayOf(
                            scheme.primary,
                            colorUtil.adjustOpacity(scheme.onSurface, ON_SURFACE_OPACITY_BUTTON_DISABLED)
                        )
                    )
                )
            }
        }
    }

    fun colorCircularProgressBarOnPrimaryContainer(progressBar: ProgressBar) {
        withScheme(progressBar) { scheme ->
            progressBar.indeterminateDrawable.setColorFilter(scheme.onPrimaryContainer, PorterDuff.Mode.SRC_ATOP)
        }
    }

    fun colorCircularProgressBar(progressBar: ProgressBar) {
        withScheme(progressBar) { scheme ->
            progressBar.indeterminateDrawable.setColorFilter(scheme.primary, PorterDuff.Mode.SRC_ATOP)
        }
    }

    fun colorCircularProgressBarOnSurfaceVariant(progressBar: ProgressBar) {
        withScheme(progressBar) { scheme ->
            progressBar.indeterminateDrawable.setColorFilter(scheme.onSurfaceVariant, PorterDuff.Mode.SRC_ATOP)
        }
    }

    fun themeCheckbox(checkbox: CheckBox) {
        withScheme(checkbox) { scheme ->
            checkbox.buttonTintList = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_checked)
                ),
                intArrayOf(Color.GRAY, scheme.primary)
            )
        }
    }

    fun themeRadioButton(radioButton: RadioButton) {
        withScheme(radioButton) { scheme ->
            radioButton.buttonTintList = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_checked)
                ),
                intArrayOf(Color.GRAY, scheme.primary)
            )
        }
    }

    fun colorEditText(editText: EditText) {
        withScheme(editText) { scheme ->
            // TODO check API-level compatibility
            // editText.background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            editText.backgroundTintList = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_focused),
                    intArrayOf(android.R.attr.state_focused)
                ),
                intArrayOf(
                    scheme.outline,
                    scheme.primary
                )
            )
            editText.setHintTextColor(scheme.onSurfaceVariant)
            editText.setTextColor(scheme.onSurface)
        }
    }

    fun highlightText(textView: TextView, originalText: String, constraint: String) {
        withScheme(textView) { scheme ->
            highlightText(textView, originalText, constraint, scheme.primary)
        }
    }

    private fun highlightText(
        textView: TextView,
        originalText: String,
        constraint: String,
        @ColorInt color: Int
    ) {
        val constraintLower = constraint.lowercase()
        val start: Int = originalText.lowercase().indexOf(constraintLower)
        if (start != -1) {
            val spanText = Spannable.Factory.getInstance().newSpannable(originalText)
            spanText(originalText, constraintLower, color, start, spanText)
            textView.setText(spanText, TextView.BufferType.SPANNABLE)
        } else {
            textView.setText(originalText, TextView.BufferType.NORMAL)
        }
    }

    private fun spanText(
        originalText: String,
        constraint: String,
        @ColorInt color: Int,
        start: Int,
        spanText: Spannable
    ) {
        var index = start
        do {
            val end = index + constraint.length
            spanText.setSpan(ForegroundColorSpan(color), index, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spanText.setSpan(StyleSpan(Typeface.BOLD), index, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            index =
                originalText.lowercase().indexOf(constraint, end + 1) // +1 skips the consecutive span
        } while (index != -1)
    }

    companion object {
        private const val ON_SURFACE_OPACITY_BUTTON_DISABLED: Float = 0.38f
    }
}
