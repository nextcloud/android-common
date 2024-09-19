/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2022-2023 Nextcloud GmbH and Nextcloud contributors
 * SPDX-FileCopyrightText: 2022-2023 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-FileCopyrightText: 2023 Stefan Niedermann <info@niedermann.it>
 * SPDX-FileCopyrightText: 2023 Unpublished <unpublished@gmx.net>
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-License-Identifier: MIT
 */
package com.nextcloud.android.common.ui.theme.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.CheckedTextView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.color.ColorUtil
import com.nextcloud.android.common.ui.theme.MaterialSchemes
import com.nextcloud.android.common.ui.theme.ViewThemeUtilsBase
import com.nextcloud.android.common.ui.util.buildColorStateList
import dynamiccolor.DynamicScheme
import dynamiccolor.MaterialDynamicColors
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * View theme utils for platform views (android.widget.*, android.view.*)
 */
@Suppress("TooManyFunctions")
class AndroidViewThemeUtils
    @Inject
    constructor(schemes: MaterialSchemes, private val colorUtil: ColorUtil) :
    ViewThemeUtilsBase(schemes) {
        private val dynamicColor = MaterialDynamicColors()

        fun colorBottomNavigationView(bottomNavigationView: BottomNavigationView) {
            withScheme(bottomNavigationView) { scheme ->
                bottomNavigationView.setBackgroundColor(dynamicColor.surface().getArgb(scheme))

                bottomNavigationView.itemIconTintList =
                    buildColorStateList(
                        android.R.attr.state_checked to dynamicColor.onSecondaryContainer().getArgb(scheme),
                        -android.R.attr.state_checked to dynamicColor.onSurfaceVariant().getArgb(scheme)
                    )

                bottomNavigationView.itemTextColor =
                    buildColorStateList(
                        android.R.attr.state_checked to dynamicColor.onSurface().getArgb(scheme),
                        -android.R.attr.state_checked to dynamicColor.onSurfaceVariant().getArgb(scheme)
                    )

                bottomNavigationView.itemActiveIndicatorColor =
                    ColorStateList
                        .valueOf(dynamicColor.secondaryContainer().getArgb(scheme))
            }
        }

        @JvmOverloads
        fun colorViewBackground(
            view: View,
            colorRole: ColorRole = ColorRole.SURFACE
        ) {
            withScheme(view) { scheme ->
                view.setBackgroundColor(colorRole.select(scheme))
            }
        }

        @JvmOverloads
        fun colorNavigationView(
            navigationView: NavigationView,
            colorIcons: Boolean = true
        ) {
            withScheme(navigationView) { scheme ->
                if (navigationView.itemBackground != null) {
                    navigationView.itemBackground!!.setTintList(
                        buildColorStateList(
                            android.R.attr.state_checked to dynamicColor.secondaryContainer().getArgb(scheme),
                            -android.R.attr.state_checked to Color.TRANSPARENT
                        )
                    )
                }
                navigationView.background.setTintList(ColorStateList.valueOf(dynamicColor.surface().getArgb(scheme)))

                val colorStateList =
                    buildColorStateList(
                        android.R.attr.state_checked to dynamicColor.onSecondaryContainer().getArgb(scheme),
                        -android.R.attr.state_checked to dynamicColor.onSurfaceVariant().getArgb(scheme)
                    )

                navigationView.itemTextColor = colorStateList
                if (colorIcons) {
                    navigationView.itemIconTintList = colorStateList
                }
            }
        }

        fun getPrimaryColorDrawable(context: Context): Drawable {
            return withScheme(context) { scheme ->
                ColorDrawable(dynamicColor.primary().getArgb(scheme))
            }
        }

        fun colorToolbarMenuIcon(
            context: Context,
            item: MenuItem
        ) {
            withScheme(context) { scheme ->
                colorMenuItemIcon(dynamicColor.onSurfaceVariant().getArgb(scheme), item)
            }
        }

        fun colorMenuItemText(
            context: Context,
            item: MenuItem
        ) {
            withScheme(context) { scheme: DynamicScheme ->
                colorMenuItemText(dynamicColor.onSurface().getArgb(scheme), item)
            }
        }

        private fun colorMenuItemIcon(
            @ColorInt color: Int,
            item: MenuItem
        ) {
            item.icon?.setTint(color)
        }

        private fun colorMenuItemText(
            @ColorInt color: Int,
            item: MenuItem
        ) {
            val newItemTitle = SpannableString(item.title)
            newItemTitle.setSpan(
                ForegroundColorSpan(color),
                0,
                newItemTitle.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            item.title = newItemTitle
        }

        @JvmOverloads
        fun tintDrawable(
            context: Context,
            @DrawableRes id: Int,
            colorRole: ColorRole = ColorRole.PRIMARY
        ): Drawable? {
            val drawable = ResourcesCompat.getDrawable(context.resources, id, null)
            return drawable?.let {
                tintDrawable(context, it, colorRole)
            }
        }

        @Deprecated(
            replaceWith =
                ReplaceWith(
                    "tintDrawable(context, id, ColorRole.PRIMARY)",
                    imports = ["com.nextcloud.android.common.ui.theme.utils.ColorRole"]
                ),
            message = "Use tintDrawable(context, id, ColorRole.PRIMARY) instead"
        )
        fun tintPrimaryDrawable(
            context: Context,
            @DrawableRes id: Int
        ): Drawable? {
            return tintDrawable(context, id, ColorRole.PRIMARY)
        }

        @JvmOverloads
        fun tintDrawable(
            context: Context,
            drawable: Drawable,
            colorRole: ColorRole = ColorRole.PRIMARY
        ): Drawable {
            return withScheme(context) { scheme: DynamicScheme ->
                colorDrawable(drawable, colorRole.select(scheme))
            }
        }

        @Deprecated(
            replaceWith =
                ReplaceWith(
                    "tintDrawable(context, drawable, ColorRole.PRIMARY)",
                    imports = ["com.nextcloud.android.common.ui.theme.utils.ColorRole"]
                ),
            message = "Use tintDrawable(context, drawable, ColorRole.PRIMARY) instead"
        )
        fun tintPrimaryDrawable(
            context: Context,
            drawable: Drawable?
        ): Drawable? {
            return drawable?.let { tintDrawable(context, it, ColorRole.PRIMARY) }
        }

        @Deprecated(
            replaceWith =
                ReplaceWith(
                    "tintDrawable(context, drawable, ColorRole.ON_SURFACE)",
                    imports = ["com.nextcloud.android.common.ui.theme.utils.ColorRole"]
                ),
            message = "Use tintDrawable(context, drawable, ColorRole.ON_SURFACE) instead"
        )
        fun tintTextDrawable(
            context: Context,
            drawable: Drawable?
        ): Drawable? {
            return drawable?.let { tintDrawable(context, it, ColorRole.ON_SURFACE) }
        }

        /**
         * Public for edge cases. For most cases use [tintDrawable] instead
         */
        fun colorDrawable(
            drawable: Drawable,
            @ColorInt color: Int
        ): Drawable {
            val wrap = DrawableCompat.wrap(drawable)
            wrap.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    color,
                    BlendModeCompat.SRC_ATOP
                )
            return wrap
        }

        fun tintToolbarArrowDrawable(
            context: Context,
            drawerToggle: ActionBarDrawerToggle,
            drawable: Drawable
        ) {
            withScheme(context) { scheme: DynamicScheme ->
                val wrap = DrawableCompat.wrap(drawable)
                wrap.colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        dynamicColor.onSurface().getArgb(scheme),
                        BlendModeCompat.SRC_ATOP
                    )
                drawerToggle.setHomeAsUpIndicator(wrap)
                drawerToggle.drawerArrowDrawable.color = dynamicColor.onSurface().getArgb(scheme)
            }
        }

        fun themeStatusBar(activity: Activity) {
            themeStatusBar(activity, ColorRole.SURFACE)
        }

        fun themeStatusBar(
            activity: Activity,
            colorRole: ColorRole
        ) {
            withScheme(activity) { scheme ->
                colorStatusBar(activity, colorRole.select(scheme))
            }
        }

        /**
         * Public for special cases, e.g. action mode. You probably want [themeStatusBar] for most cases instead.
         */
        fun colorStatusBar(
            activity: Activity,
            @ColorInt color: Int
        ) {
            val window = activity.window ?: return
            val isLightBackground = !colorUtil.isDarkBackground(color)
            val decor = window.decorView
            window.statusBarColor = color
            window.navigationBarColor = color
            WindowInsetsControllerCompat(window, decor).isAppearanceLightStatusBars = isLightBackground
            WindowInsetsControllerCompat(window, decor).isAppearanceLightNavigationBars = isLightBackground
        }

        fun resetStatusBar(activity: Activity) {
            colorStatusBar(
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
                view.setBackgroundColor(dynamicColor.surface().getArgb(scheme))
            }
        }

        fun themeDialogDark(view: View) {
            withSchemeDark { scheme ->
                view.setBackgroundColor(dynamicColor.surface().getArgb(scheme))
            }
        }

        fun themeDialogDivider(view: View) {
            withScheme(view) { scheme ->
                view.setBackgroundColor(dynamicColor.surfaceVariant().getArgb(scheme))
            }
        }

        fun themeHorizontalSeekBar(seekBar: SeekBar) {
            withScheme(seekBar) { scheme ->
                themeHorizontalProgressBar(seekBar, dynamicColor.primary().getArgb(scheme))
                seekBar.thumb.colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        dynamicColor.primary().getArgb(scheme),
                        BlendModeCompat.SRC_IN
                    )
            }
        }

        fun themeHorizontalProgressBar(progressBar: ProgressBar) {
            withScheme(progressBar) { scheme ->
                themeHorizontalProgressBar(progressBar, dynamicColor.primary().getArgb(scheme))
            }
        }

        fun themeHorizontalProgressBar(
            progressBar: ProgressBar?,
            @ColorInt color: Int
        ) {
            progressBar?.indeterminateDrawable?.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    color,
                    BlendModeCompat.SRC_IN
                )
            progressBar?.progressDrawable?.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    color,
                    BlendModeCompat.SRC_IN
                )
        }

        @JvmOverloads
        fun colorTextView(
            textView: TextView,
            colorRole: ColorRole = ColorRole.PRIMARY
        ) {
            withScheme(textView) { scheme ->
                textView.setTextColor(colorRole.select(scheme))
            }
        }

        @JvmOverloads
        fun colorTextViewLinks(
            textView: TextView,
            colorRole: ColorRole = ColorRole.PRIMARY
        ) {
            withScheme(textView.context) { scheme ->
                textView.setLinkTextColor(colorRole.select(scheme))
            }
        }

        @Deprecated(
            replaceWith = ReplaceWith("colorTextView(textView)"),
            message = "Use colorTextView(textView) instead"
        )
        fun colorPrimaryTextViewElement(textView: TextView) {
            colorTextView(textView, ColorRole.PRIMARY)
        }

        @Deprecated(
            replaceWith =
                ReplaceWith(
                    "colorTextView(textView, ColorRole.ON_SECONDARY_CONTAINER)",
                    imports = ["com.nextcloud.android.common.ui.theme.utils.ColorRole"]
                ),
            message = "Use colorTextView(textView, ColorRole.ON_SECONDARY_CONTAINER) instead"
        )
        fun colorOnSecondaryContainerTextViewElement(textView: TextView) {
            colorTextView(textView, ColorRole.ON_SECONDARY_CONTAINER)
        }

        fun colorPrimaryTextViewElementDarkMode(textView: TextView) {
            withSchemeDark { scheme ->
                textView.setTextColor(dynamicColor.primary().getArgb(scheme))
            }
        }

        @Deprecated(
            replaceWith = ReplaceWith("colorViewBackground(view)"),
            message = "Use colorViewBackground(view) instead"
        )
        fun colorPrimaryView(view: View) {
            withScheme(view) { scheme ->
                view.setBackgroundColor(dynamicColor.primary().getArgb(scheme))
            }
        }

        /**
         * Colors the background as element color and the foreground as text color.
         *
         */
        @Deprecated(
            replaceWith = ReplaceWith("colorImageViewBackgroundAndIcon"),
            message = "Use colorImageViewBackgroundAndIcon, which has a better name, instead"
        )
        fun colorImageViewButton(imageView: ImageView) {
            colorImageViewBackgroundAndIcon(imageView)
        }

        /**
         * Colors the background as element color and the foreground as text color.
         */
        fun colorImageViewBackgroundAndIcon(imageView: ImageView) {
            withScheme(imageView) { scheme ->
                imageView.imageTintList = ColorStateList.valueOf(dynamicColor.onPrimaryContainer().getArgb(scheme))
                imageView.backgroundTintList = ColorStateList.valueOf(dynamicColor.primaryContainer().getArgb(scheme))
            }
        }

        fun themeImageButton(imageButton: ImageButton) {
            withScheme(imageButton) { scheme ->
                imageButton.imageTintList =
                    buildColorStateList(
                        android.R.attr.state_selected to dynamicColor.primary().getArgb(scheme),
                        -android.R.attr.state_selected to dynamicColor.onSurfaceVariant().getArgb(scheme),
                        android.R.attr.state_enabled to dynamicColor.onSurfaceVariant().getArgb(scheme),
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                dynamicColor.onSurface().getArgb(scheme),
                                ON_SURFACE_OPACITY_BUTTON_DISABLED
                            )
                    )
            }
        }

        /**
         * In most cases you'll want to use [themeImageButton] instead.
         */
        fun colorImageButton(
            imageButton: ImageButton?,
            @ColorInt color: Int
        ) {
            imageButton?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }

        /**
         * Tints the image with element color
         */
        @Deprecated(
            replaceWith = ReplaceWith("colorImageView(imageView, ColorRole.PRIMARY)"),
            message = "Use colorImageView(imageView, ColorRole.PRIMARY) instead"
        )
        fun colorImageView(imageView: ImageView) {
            colorImageView(imageView, ColorRole.PRIMARY)
        }

        fun colorImageView(
            imageView: ImageView,
            colorRole: ColorRole
        ) {
            withScheme(imageView) { scheme ->
                imageView.imageTintList = ColorStateList.valueOf(colorRole.select(scheme))
            }
        }

        fun colorTextButtons(vararg buttons: Button) {
            withScheme(buttons[0]) { scheme ->
                colorTextButtons(dynamicColor.primary().getArgb(scheme), *buttons)
            }
        }

        /**
         * In most cases you'll want to use [colorTextButtons] instead.
         */
        fun colorTextButtons(
            @ColorInt color: Int,
            vararg buttons: Button
        ) {
            withScheme(buttons[0]) { scheme ->
                for (button in buttons) {
                    button.setTextColor(
                        buildColorStateList(
                            android.R.attr.state_enabled to color,
                            -android.R.attr.state_enabled to
                                colorUtil.adjustOpacity(
                                    dynamicColor.onSurface().getArgb(scheme),
                                    ON_SURFACE_OPACITY_BUTTON_DISABLED
                                )
                        )
                    )
                }
            }
        }

        @Deprecated(
            replaceWith =
                ReplaceWith(
                    "colorCircularProgressBar(progressBar, ColorRole.ON_PRIMARY_CONTAINER)",
                    imports = ["com.nextcloud.android.common.ui.theme.utils.ColorRole"]
                ),
            message = "Use colorCircularProgressBar(progressBar, ColorRole.ON_PRIMARY_CONTAINER) instead"
        )
        fun colorCircularProgressBarOnPrimaryContainer(progressBar: ProgressBar) {
            colorCircularProgressBar(progressBar, ColorRole.ON_PRIMARY_CONTAINER)
        }

        @Deprecated(
            replaceWith =
                ReplaceWith(
                    "colorCircularProgressBar(progressBar, ColorRole.PRIMARY)",
                    imports = ["com.nextcloud.android.common.ui.theme.utils.ColorRole"]
                ),
            message = "Use colorCircularProgressBar(progressBar, ColorRole.PRIMARY) instead"
        )
        fun colorCircularProgressBar(progressBar: ProgressBar) {
            colorCircularProgressBar(progressBar, ColorRole.PRIMARY)
        }

        @Deprecated(
            replaceWith =
                ReplaceWith(
                    "colorCircularProgressBar(progressBar, ColorRole.ON_SURFACE_VARIANT)",
                    imports = ["com.nextcloud.android.common.ui.theme.utils.ColorRole"]
                ),
            message = "Use colorCircularProgressBar(progressBar, ColorRole.ON_SURFACE_VARIANT) instead"
        )
        fun colorCircularProgressBarOnSurfaceVariant(progressBar: ProgressBar) {
            colorCircularProgressBar(progressBar, ColorRole.ON_SURFACE_VARIANT)
        }

        fun colorCircularProgressBar(
            progressBar: ProgressBar,
            colorRole: ColorRole
        ) {
            withScheme(progressBar) { scheme ->
                progressBar.indeterminateDrawable.colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        colorRole.select(scheme),
                        BlendModeCompat.SRC_ATOP
                    )
            }
        }

        fun themeCheckedTextView(vararg checkedTextViews: CheckedTextView) {
            withScheme(checkedTextViews[0]) { scheme ->
                val colorStateList =
                    buildColorStateList(
                        -android.R.attr.state_checked to Color.GRAY,
                        -android.R.attr.state_enabled to Color.GRAY,
                        android.R.attr.state_checked to dynamicColor.primary().getArgb(scheme)
                    )

                checkedTextViews.forEach {
                    it.checkMarkTintList = colorStateList
                }
            }
        }

        fun themeCheckbox(vararg checkboxes: CheckBox) {
            withScheme(checkboxes[0]) { scheme ->
                val colorStateList =
                    buildColorStateList(
                        -android.R.attr.state_checked to Color.GRAY,
                        -android.R.attr.state_enabled to Color.GRAY,
                        android.R.attr.state_checked to dynamicColor.primary().getArgb(scheme)
                    )
                checkboxes.forEach {
                    it.buttonTintList = colorStateList
                }
            }
        }

        fun themeRadioButton(radioButton: RadioButton) {
            withScheme(radioButton) { scheme ->
                radioButton.buttonTintList =
                    buildColorStateList(
                        -android.R.attr.state_checked to Color.GRAY,
                        android.R.attr.state_checked to dynamicColor.primary().getArgb(scheme)
                    )
            }
        }

        fun colorEditText(editText: EditText) {
            withScheme(editText) { scheme ->
                // TODO check API-level compatibility
                // editText.background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                editText.backgroundTintList =
                    buildColorStateList(
                        -android.R.attr.state_focused to dynamicColor.outline().getArgb(scheme),
                        android.R.attr.state_focused to dynamicColor.primary().getArgb(scheme)
                    )

                editText.setHintTextColor(dynamicColor.onSurfaceVariant().getArgb(scheme))
                editText.setTextColor(dynamicColor.onSurface().getArgb(scheme))
            }
        }

        fun colorEditTextOnPrimary(editText: EditText) {
            withScheme(editText) { scheme ->
                // TODO check API-level compatibility
                editText.setHintTextColor(dynamicColor.onPrimary().getArgb(scheme))
                editText.setTextColor(dynamicColor.onPrimary().getArgb(scheme))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    editText.textCursorDrawable?.let {
                        editText.textCursorDrawable = colorDrawable(it, dynamicColor.onPrimary().getArgb(scheme))
                    }
                }
            }
        }

        @JvmOverloads
        fun highlightText(
            textView: TextView,
            originalText: String,
            constraint: String,
            colorRole: ColorRole = ColorRole.PRIMARY
        ) {
            withScheme(textView) { scheme ->
                highlightText(textView, originalText, constraint, colorRole.select(scheme))
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
            val matcher =
                Pattern
                    .compile(constraint, Pattern.CASE_INSENSITIVE or Pattern.LITERAL)
                    .matcher(originalText)

            matcher.find(start)

            do {
                val mStart = matcher.start()
                val mEnd = matcher.end()
                spanText.setSpan(ForegroundColorSpan(color), mStart, mEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spanText.setSpan(StyleSpan(Typeface.BOLD), mStart, mEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            } while (matcher.find())
        }

        // here for backwards compatibility
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        fun colorSwitch(switch: Switch) {
            withScheme(switch) { scheme ->
                val colors = SwitchColorUtils.calculateSwitchColors(switch.context, scheme)
                DrawableCompat.setTintList(switch.thumbDrawable, colors.thumbColor)
                DrawableCompat.setTintList(switch.trackDrawable, colors.trackColor)
            }
        }

        @Deprecated("Don't do this, implement custom viewThemeUtils instead")
        fun primaryColor(activity: Activity): Int {
            return withScheme(activity) { scheme ->
                dynamicColor.primary().getArgb(scheme)
            }
        }

        companion object {
            private const val ON_SURFACE_OPACITY_BUTTON_DISABLED: Float = 0.38f
        }
    }
