/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2022-2023 Nextcloud GmbH and Nextcloud contributors
 * SPDX-FileCopyrightText: 2022-2023 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-FileCopyrightText: 2023 Stefan Niedermann <info@niedermann.it>
 * SPDX-FileCopyrightText: 2023 Thore Goebel <goebel.thore@gmail.com>
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-License-Identifier: MIT
 */
package com.nextcloud.android.common.ui.theme.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDragHandleView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.color.ColorUtil
import com.nextcloud.android.common.ui.theme.MaterialSchemes
import com.nextcloud.android.common.ui.theme.ViewThemeUtilsBase
import com.nextcloud.android.common.ui.util.buildColorStateList
import scheme.Scheme
import javax.inject.Inject

/**
 * View theme utils for Material views (com.google.android.material.*)
 */
@Suppress("TooManyFunctions")
class MaterialViewThemeUtils
    @Inject
    constructor(schemes: MaterialSchemes, private val colorUtil: ColorUtil) :
    ViewThemeUtilsBase(schemes) {
        fun colorToolbarOverflowIcon(toolbar: MaterialToolbar) {
            withScheme(toolbar) { scheme ->
                colorTrailingIcon(scheme, toolbar.overflowIcon)
            }
        }

        private fun colorTrailingIcon(
            scheme: Scheme,
            icon: Drawable?
        ) {
            icon?.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    scheme.onSurfaceVariant,
                    BlendModeCompat.SRC_ATOP
                )
        }

        fun themeSearchBarText(searchText: MaterialTextView) {
            withScheme(searchText) { scheme ->
                searchText.setHintTextColor(scheme.onSurfaceVariant)
            }
        }

        fun themeFAB(fab: FloatingActionButton) {
            withScheme(fab) { scheme ->
                fab.backgroundTintList =
                    buildColorStateList(
                        android.R.attr.state_enabled to scheme.primaryContainer,
                        -android.R.attr.state_enabled to Color.GRAY
                    )

                fab.imageTintList =
                    buildColorStateList(
                        android.R.attr.state_enabled to scheme.onPrimaryContainer,
                        -android.R.attr.state_enabled to Color.WHITE
                    )
            }
        }

        fun themeSecondaryFAB(fab: FloatingActionButton) {
            withScheme(fab) { scheme ->
                fab.backgroundTintList =
                    buildColorStateList(
                        android.R.attr.state_enabled to scheme.secondaryContainer,
                        -android.R.attr.state_enabled to Color.GRAY
                    )

                fab.imageTintList =
                    buildColorStateList(
                        android.R.attr.state_enabled to scheme.onSecondaryContainer,
                        -android.R.attr.state_enabled to Color.WHITE
                    )
            }
        }

        fun themeExtendedFAB(fab: ExtendedFloatingActionButton) {
            withScheme(fab) { scheme ->
                fab.backgroundTintList =
                    buildColorStateList(
                        android.R.attr.state_enabled to scheme.primaryContainer,
                        -android.R.attr.state_enabled to Color.GRAY
                    )

                val colorStateList =
                    buildColorStateList(
                        android.R.attr.state_enabled to scheme.onPrimaryContainer,
                        -android.R.attr.state_enabled to Color.WHITE
                    )
                fab.setTextColor(colorStateList)
                fab.iconTint = colorStateList
            }
        }

        fun themeCardView(cardView: MaterialCardView) {
            withScheme(cardView) { scheme ->
                cardView.backgroundTintList = ColorStateList.valueOf(scheme.surface)
                cardView.setStrokeColor(
                    buildColorStateList(
                        android.R.attr.state_checked to scheme.primary,
                        -android.R.attr.state_checked to scheme.outline
                    )
                )
            }
        }

        fun themeDragHandleView(dragHandleView: BottomSheetDragHandleView) {
            withScheme(dragHandleView) { scheme ->
                dragHandleView.imageTintList = ColorStateList.valueOf(scheme.onSurfaceVariant)
            }
        }

        fun colorMaterialTextButton(button: MaterialButton) {
            withScheme(button) { scheme ->
                button.rippleColor = rippleColor(scheme)
            }
        }

        fun colorMaterialButtonText(button: MaterialButton) {
            withScheme(button) { scheme ->
                val disabledColor = ContextCompat.getColor(button.context, R.color.disabled_text)
                val colorStateList =
                    buildColorStateList(
                        android.R.attr.state_enabled to scheme.primary,
                        -android.R.attr.state_enabled to disabledColor
                    )
                button.setTextColor(colorStateList)
                button.iconTint = colorStateList
            }
        }

        fun colorMaterialButtonPrimaryFilled(button: MaterialButton) {
            withScheme(button) { scheme ->
                button.backgroundTintList =
                    buildColorStateList(
                        android.R.attr.state_enabled to scheme.primary,
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                scheme.onSurface,
                                SURFACE_OPACITY_BUTTON_DISABLED
                            )
                    )

                val contentColorList =
                    buildColorStateList(
                        android.R.attr.state_enabled to scheme.onPrimary,
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                scheme.onSurface,
                                SURFACE_OPACITY_BUTTON_DISABLED
                            )
                    )

                button.setTextColor(contentColorList)
                button.iconTint = contentColorList
            }
        }

        fun colorMaterialButtonPrimaryTonal(button: MaterialButton) {
            withScheme(button) { scheme ->
                button.backgroundTintList =
                    buildColorStateList(
                        android.R.attr.state_enabled to scheme.secondaryContainer,
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                scheme.onSurface,
                                SURFACE_OPACITY_BUTTON_DISABLED
                            ),
                        -android.R.attr.state_hovered to scheme.onSecondaryContainer,
                        -android.R.attr.state_focused to scheme.onSecondaryContainer,
                        -android.R.attr.state_pressed to scheme.onSecondaryContainer
                    )

                val contentColorList =
                    buildColorStateList(
                        android.R.attr.state_enabled to scheme.onSecondaryContainer,
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                scheme.onSurface,
                                ON_SURFACE_OPACITY_BUTTON_DISABLED
                            ),
                        -android.R.attr.state_hovered to scheme.onSecondaryContainer,
                        -android.R.attr.state_focused to scheme.onSecondaryContainer,
                        -android.R.attr.state_pressed to scheme.onSecondaryContainer
                    )
                button.setTextColor(contentColorList)
                button.iconTint = contentColorList
            }
        }

        fun colorMaterialButtonPrimaryOutlined(button: MaterialButton) {
            withScheme(button) { scheme ->
                val contentColorList =
                    buildColorStateList(
                        android.R.attr.state_enabled to scheme.primary,
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                scheme.onSurface,
                                ON_SURFACE_OPACITY_BUTTON_DISABLED
                            )
                    )

                button.setTextColor(contentColorList)
                button.iconTint = contentColorList
                button.strokeColor =
                    buildColorStateList(
                        android.R.attr.state_enabled to scheme.outline,
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                scheme.onSurface,
                                ON_SURFACE_OPACITY_BUTTON_OUTLINE_DISABLED
                            ),
                        -android.R.attr.state_hovered to scheme.outline,
                        -android.R.attr.state_focused to scheme.primary,
                        -android.R.attr.state_pressed to scheme.outline
                    )

                button.strokeWidth =
                    button.resources.getDimension(R.dimen.outlinedButtonStrokeWidth).toInt()
                button.rippleColor = rippleColor(scheme)
            }
        }

        fun colorMaterialButtonPrimaryBorderless(button: MaterialButton) {
            withScheme(button) { scheme ->
                val contentColorList =
                    buildColorStateList(
                        android.R.attr.state_enabled to scheme.primary,
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                scheme.onSurface,
                                ON_SURFACE_OPACITY_BUTTON_DISABLED
                            )
                    )

                button.setTextColor(contentColorList)
                button.iconTint = contentColorList
            }
        }

        /**
         * text is primary, background is on_primary
         */
        fun colorMaterialButtonFilledOnPrimary(button: MaterialButton) {
            withScheme(button) { scheme ->
                button.backgroundTintList =
                    buildColorStateList(
                        android.R.attr.state_enabled to scheme.onPrimary,
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                scheme.surface,
                                SURFACE_OPACITY_BUTTON_DISABLED
                            ),
                        -android.R.attr.state_hovered to scheme.onPrimary,
                        -android.R.attr.state_focused to scheme.onPrimary,
                        -android.R.attr.state_pressed to scheme.onPrimary
                    )

                val contentColorList =
                    buildColorStateList(
                        android.R.attr.state_enabled to scheme.primary,
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                scheme.primary,
                                ON_SURFACE_OPACITY_BUTTON_DISABLED
                            ),
                        -android.R.attr.state_hovered to scheme.primary,
                        -android.R.attr.state_focused to scheme.primary,
                        -android.R.attr.state_pressed to scheme.primary
                    )

                button.setTextColor(
                    contentColorList
                )

                button.iconTint = contentColorList
            }
        }

        fun colorMaterialButtonOutlinedOnPrimary(button: MaterialButton) {
            withScheme(button) { scheme ->
                button.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
                val contentColorList =
                    buildColorStateList(
                        android.R.attr.state_enabled to scheme.onPrimary,
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                scheme.onPrimary,
                                ON_SURFACE_OPACITY_BUTTON_DISABLED
                            )
                    )

                button.setTextColor(contentColorList)
                button.iconTint = contentColorList
                button.strokeColor = contentColorList
                button.strokeWidth =
                    button.resources.getDimension(R.dimen.outlinedButtonStrokeWidth).toInt()
                button.rippleColor = rippleColor(scheme)
            }
        }

        fun themeToolbar(toolbar: MaterialToolbar) {
            withScheme(toolbar) { scheme ->
                toolbar.setBackgroundColor(scheme.surface)
                toolbar.setNavigationIconTint(scheme.onSurface)
                toolbar.setTitleTextColor(scheme.onSurface)
                colorTrailingIcon(scheme, toolbar.overflowIcon)
            }
        }

        @Deprecated(
            "Duplicated, use themeCardView instead",
            replaceWith = ReplaceWith("themeCardView(card)")
        )
        fun colorCardViewBackground(card: MaterialCardView) = themeCardView(card)

        fun colorProgressBar(progressIndicator: LinearProgressIndicator) {
            withScheme(progressIndicator) { scheme ->
                colorProgressBar(progressIndicator, scheme.primary)
            }
        }

        fun colorProgressBar(
            progressIndicator: LinearProgressIndicator,
            @ColorInt color: Int
        ) {
            progressIndicator.setIndicatorColor(color)
        }

        fun colorProgressBar(progressIndicator: CircularProgressIndicator) {
            withScheme(progressIndicator) { scheme ->
                colorProgressBar(progressIndicator, scheme.primary)
            }
        }

        fun colorProgressBar(
            progressIndicator: CircularProgressIndicator,
            @ColorInt color: Int
        ) {
            progressIndicator.setIndicatorColor(color)
        }

        fun colorTextInputLayout(textInputLayout: TextInputLayout) {
            withScheme(textInputLayout) { scheme ->
                val errorColor = scheme.onSurfaceVariant

                val errorColorStateList =
                    buildColorStateList(
                        -android.R.attr.state_focused to errorColor,
                        android.R.attr.state_focused to errorColor
                    )

                val coloredColorStateList =
                    buildColorStateList(
                        -android.R.attr.state_focused to scheme.outline,
                        android.R.attr.state_focused to scheme.primary
                    )

                textInputLayout.setBoxStrokeColorStateList(coloredColorStateList)
                textInputLayout.setErrorIconTintList(errorColorStateList)
                textInputLayout.setErrorTextColor(errorColorStateList)
                textInputLayout.boxStrokeErrorColor = errorColorStateList
                textInputLayout.defaultHintTextColor = coloredColorStateList

                textInputLayout.editText?.highlightColor = scheme.primary
            }
        }

        fun colorTextInputLayout(
            textInputLayout: TextInputLayout,
            colorRole: ColorRole
        ) {
            withScheme(textInputLayout) { scheme ->
                val errorColor = scheme.error

                val errorColorStateList =
                    buildColorStateList(
                        -android.R.attr.state_focused to errorColor,
                        android.R.attr.state_focused to errorColor
                    )

                val coloredColorStateList =
                    buildColorStateList(
                        -android.R.attr.state_focused to scheme.outline,
                        android.R.attr.state_focused to colorRole.select(scheme)
                    )

                textInputLayout.setBoxStrokeColorStateList(coloredColorStateList)
                textInputLayout.setErrorIconTintList(errorColorStateList)
                textInputLayout.setErrorTextColor(errorColorStateList)
                textInputLayout.boxStrokeErrorColor = errorColorStateList
                textInputLayout.defaultHintTextColor = coloredColorStateList

                textInputLayout.editText?.highlightColor = colorRole.select(scheme)
                textInputLayout.setEndIconTintList(coloredColorStateList)
                textInputLayout.setStartIconTintList(coloredColorStateList)
            }
        }

        fun themeTabLayout(tabLayout: TabLayout) {
            withScheme(tabLayout) { scheme ->
                colorTabLayout(tabLayout, scheme)
            }
        }

        fun themeTabLayoutOnSurface(tabLayout: TabLayout) {
            withScheme(tabLayout) { scheme ->
                tabLayout.setBackgroundColor(scheme.surface)
                colorTabLayout(tabLayout, scheme)
            }
        }

        fun colorTabLayout(
            tabLayout: TabLayout,
            scheme: Scheme
        ) {
            tabLayout.setSelectedTabIndicatorColor(scheme.primary)
            val tabContentColors =
                buildColorStateList(
                    android.R.attr.state_selected to scheme.primary,
                    -android.R.attr.state_selected to
                        ContextCompat.getColor(
                            tabLayout.context,
                            R.color.high_emphasis_text
                        )
                )

            tabLayout.tabTextColors = tabContentColors
            tabLayout.tabIconTint = tabContentColors
            tabLayout.tabRippleColor = rippleColor(scheme)
        }

        fun colorMaterialCheckBox(materialCheckBox: MaterialCheckBox) {
            withScheme(materialCheckBox.context) { scheme ->
                materialCheckBox.buttonTintList =
                    buildColorStateList(
                        android.R.attr.state_checked to scheme.primary,
                        -android.R.attr.state_checked to scheme.outline
                    )

                materialCheckBox.buttonIconTintList =
                    buildColorStateList(
                        android.R.attr.state_checked to scheme.onPrimary,
                        -android.R.attr.state_checked to android.R.color.transparent
                    )
            }
        }

        fun colorMaterialSwitch(materialSwitch: MaterialSwitch) {
            withScheme(materialSwitch.context) { scheme ->
                materialSwitch.thumbTintList =
                    buildColorStateList(
                        android.R.attr.state_checked to scheme.onPrimary,
                        -android.R.attr.state_checked to scheme.outline
                    )

                materialSwitch.trackTintList =
                    buildColorStateList(
                        android.R.attr.state_checked to scheme.primary,
                        // XXX: specs use surfaceContainerHighest
                        -android.R.attr.state_checked to scheme.surface
                    )

                materialSwitch.trackDecorationTintList =
                    buildColorStateList(
                        android.R.attr.state_checked to android.R.color.transparent,
                        -android.R.attr.state_checked to scheme.outline
                    )
            }
        }

        fun colorChipBackground(chip: Chip) {
            withScheme(chip) { scheme ->
                chip.chipBackgroundColor = ColorStateList.valueOf(scheme.primary)
                chip.setTextColor(scheme.onPrimary)
            }
        }

        fun colorChipDrawable(
            context: Context,
            chipDrawable: ChipDrawable
        ) {
            withScheme(context) { scheme ->
                chipDrawable.chipBackgroundColor = ColorStateList.valueOf(scheme.primary)
                chipDrawable.setTextColor(scheme.onPrimary)
            }
        }

        fun colorChipOutlined(
            chip: Chip,
            strokeWidth: Float
        ) {
            withScheme(chip) { scheme ->
                chip.chipBackgroundColor = ColorStateList.valueOf(Color.TRANSPARENT)
                chip.chipStrokeWidth = strokeWidth
                chip.chipStrokeColor = ColorStateList.valueOf(scheme.primary)
                chip.setTextColor(scheme.primary)
            }
        }

        fun themeSnackbar(snackbar: Snackbar) {
            withScheme(snackbar.context) { scheme ->
                snackbar.setBackgroundTint(scheme.inverseSurface)
                snackbar.setActionTextColor(scheme.inversePrimary)
                snackbar.setTextColor(scheme.inverseOnSurface)
            }
        }

        fun themeChipSuggestion(chip: Chip) {
            withScheme(chip.context) { scheme ->
                chip.chipStrokeColor = chipOutlineColorList(scheme)
                chip.setTextColor(chipSuggestionInputTextColorList(scheme))
            }
        }

        fun themeChipFilter(chip: Chip) {
            withScheme(chip.context) { scheme ->
                val backgroundColors =
                    buildColorStateList(
                        android.R.attr.state_checked to scheme.secondaryContainer,
                        -android.R.attr.state_checked to scheme.surface,
                        android.R.attr.state_focused to scheme.secondaryContainer,
                        android.R.attr.state_hovered to scheme.secondaryContainer,
                        android.R.attr.state_pressed to scheme.secondaryContainer
                    )

                val iconColors =
                    buildColorStateList(
                        android.R.attr.state_checked to scheme.onSecondaryContainer,
                        -android.R.attr.state_checked to scheme.onSurfaceVariant,
                        android.R.attr.state_focused to scheme.onSecondaryContainer,
                        android.R.attr.state_hovered to scheme.onSecondaryContainer,
                        android.R.attr.state_pressed to scheme.onSecondaryContainer
                    )

                val textColors =
                    buildColorStateList(
                        android.R.attr.state_checked to scheme.onSecondaryContainer,
                        -android.R.attr.state_checked to scheme.onSurfaceVariant,
                        android.R.attr.state_hovered to scheme.onSecondaryContainer,
                        android.R.attr.state_focused to scheme.onSecondaryContainer,
                        android.R.attr.state_pressed to scheme.onSecondaryContainer
                    )

                chip.chipBackgroundColor = backgroundColors
                chip.chipStrokeColor = chipOutlineFilterColorList(scheme)
                chip.setTextColor(textColors)
                chip.checkedIconTint = iconColors
            }
        }

        fun themeChipAssist(chip: Chip) {
            withScheme(chip.context) { scheme ->
                val iconColors =
                    buildColorStateList(
                        android.R.attr.state_enabled to scheme.primary,
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                scheme.onSurface,
                                ON_SURFACE_OPACITY_BUTTON_DISABLED
                            ),
                        android.R.attr.state_focused to scheme.primary,
                        android.R.attr.state_hovered to scheme.primary,
                        android.R.attr.state_pressed to scheme.primary
                    )

                val textColors =
                    buildColorStateList(
                        android.R.attr.state_enabled to scheme.onSurface,
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                scheme.onSurface,
                                ON_SURFACE_OPACITY_BUTTON_DISABLED
                            ),
                        android.R.attr.state_hovered to scheme.onSurface,
                        android.R.attr.state_focused to scheme.onSurface,
                        android.R.attr.state_pressed to scheme.onSurface
                    )

                chip.chipStrokeColor = chipOutlineColorList(scheme)
                chip.setTextColor(textColors)
                chip.chipIconTint = iconColors
            }
        }

        fun themeChipInput(chip: Chip) {
            withScheme(chip.context) { scheme ->
                val iconColors =
                    buildColorStateList(
                        android.R.attr.state_enabled to scheme.onSurfaceVariant,
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                scheme.onSurface,
                                ON_SURFACE_OPACITY_BUTTON_DISABLED
                            ),
                        android.R.attr.state_focused to scheme.onSurfaceVariant,
                        android.R.attr.state_hovered to scheme.onSurfaceVariant,
                        android.R.attr.state_pressed to scheme.onSurfaceVariant
                    )

                chip.chipStrokeColor = chipOutlineFilterColorList(scheme)
                chip.setTextColor(chipSuggestionInputTextColorList(scheme))
                chip.chipIconTint = iconColors
            }
        }

        private fun chipOutlineColorList(scheme: Scheme) =
            buildColorStateList(
                android.R.attr.state_enabled to scheme.outline,
                -android.R.attr.state_enabled to
                    colorUtil.adjustOpacity(
                        scheme.onSurface,
                        ON_SURFACE_OPACITY_BUTTON_OUTLINE_DISABLED
                    ),
                android.R.attr.state_hovered to scheme.outline,
                android.R.attr.state_focused to scheme.onSurfaceVariant,
                android.R.attr.state_pressed to scheme.outline
            )

        private fun chipOutlineFilterColorList(scheme: Scheme) =
            buildColorStateList(
                android.R.attr.state_checked to scheme.secondaryContainer,
                -android.R.attr.state_checked to scheme.outline
            )

        private fun chipSuggestionInputTextColorList(scheme: Scheme) =
            buildColorStateList(
                android.R.attr.state_enabled to scheme.onSurfaceVariant,
                -android.R.attr.state_enabled to
                    colorUtil.adjustOpacity(
                        scheme.onSurface,
                        ON_SURFACE_OPACITY_BUTTON_DISABLED
                    ),
                android.R.attr.state_hovered to scheme.onSurfaceVariant,
                android.R.attr.state_focused to scheme.onSurfaceVariant,
                android.R.attr.state_pressed to scheme.onSurfaceVariant
            )

        private fun rippleColor(scheme: Scheme) =
            buildColorStateList(
                android.R.attr.state_pressed to
                    colorUtil.adjustOpacity(
                        scheme.primary,
                        SURFACE_OPACITY_BUTTON_DISABLED
                    )
            )

        companion object {
            private const val SURFACE_OPACITY_BUTTON_DISABLED: Float = 0.12f
            private const val ON_SURFACE_OPACITY_BUTTON_OUTLINE_DISABLED: Float = 0.12f
            private const val ON_SURFACE_OPACITY_BUTTON_DISABLED: Float = 0.38f
        }
    }
