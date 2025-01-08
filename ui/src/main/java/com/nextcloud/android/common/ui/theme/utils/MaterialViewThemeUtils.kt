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
import android.view.View
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
import dynamiccolor.DynamicScheme
import dynamiccolor.MaterialDynamicColors
import javax.inject.Inject

/**
 * View theme utils for Material views (com.google.android.material.*)
 */
@Suppress("TooManyFunctions")
class MaterialViewThemeUtils
    @Inject
    constructor(
        schemes: MaterialSchemes,
        private val colorUtil: ColorUtil
    ) : ViewThemeUtilsBase(schemes) {
        private val dynamicColor = MaterialDynamicColors()

        fun colorToolbarOverflowIcon(toolbar: MaterialToolbar) {
            withScheme(toolbar) { scheme ->
                colorTrailingIcon(scheme, toolbar.overflowIcon)
            }
        }

        private fun colorTrailingIcon(
            scheme: DynamicScheme,
            icon: Drawable?
        ) {
            icon?.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    dynamicColor.onSurfaceVariant().getArgb(scheme),
                    BlendModeCompat.SRC_ATOP
                )
        }

        fun themeSearchBarText(searchText: MaterialTextView) {
            withScheme(searchText) { scheme ->
                searchText.setHintTextColor(dynamicColor.surfaceVariant().getArgb(scheme))
            }
        }

        fun themeFAB(fab: FloatingActionButton) {
            withScheme(fab) { scheme ->
                fab.backgroundTintList =
                    buildColorStateList(
                        android.R.attr.state_enabled to dynamicColor.primaryContainer().getArgb(scheme),
                        -android.R.attr.state_enabled to Color.GRAY
                    )

                fab.imageTintList =
                    buildColorStateList(
                        android.R.attr.state_enabled to dynamicColor.onPrimaryContainer().getArgb(scheme),
                        -android.R.attr.state_enabled to Color.WHITE
                    )
            }
        }

        fun themeSecondaryFAB(fab: FloatingActionButton) {
            withScheme(fab) { scheme ->
                fab.backgroundTintList =
                    buildColorStateList(
                        android.R.attr.state_enabled to dynamicColor.secondaryContainer().getArgb(scheme),
                        -android.R.attr.state_enabled to Color.GRAY
                    )

                fab.imageTintList =
                    buildColorStateList(
                        android.R.attr.state_enabled to dynamicColor.onSecondaryContainer().getArgb(scheme),
                        -android.R.attr.state_enabled to Color.WHITE
                    )
            }
        }

        fun themeExtendedFAB(fab: ExtendedFloatingActionButton) {
            withScheme(fab) { scheme ->
                fab.backgroundTintList =
                    buildColorStateList(
                        android.R.attr.state_enabled to dynamicColor.primaryContainer().getArgb(scheme),
                        -android.R.attr.state_enabled to Color.GRAY
                    )

                val colorStateList =
                    buildColorStateList(
                        android.R.attr.state_enabled to dynamicColor.onPrimaryContainer().getArgb(scheme),
                        -android.R.attr.state_enabled to Color.WHITE
                    )
                fab.setTextColor(colorStateList)
                fab.iconTint = colorStateList
            }
        }

        fun themeCardView(cardView: MaterialCardView) {
            withScheme(cardView) { scheme ->
                cardView.backgroundTintList = ColorStateList.valueOf(dynamicColor.surface().getArgb(scheme))
                cardView.setStrokeColor(
                    buildColorStateList(
                        android.R.attr.state_checked to dynamicColor.primary().getArgb(scheme),
                        -android.R.attr.state_checked to dynamicColor.outline().getArgb(scheme)
                    )
                )
            }
        }

        fun themeDragHandleView(dragHandleView: BottomSheetDragHandleView) {
            withScheme(dragHandleView) { scheme ->
                dragHandleView.imageTintList = ColorStateList.valueOf(dynamicColor.onSurfaceVariant().getArgb(scheme))
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
                        android.R.attr.state_enabled to dynamicColor.primary().getArgb(scheme),
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
                        android.R.attr.state_enabled to dynamicColor.primary().getArgb(scheme),
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                dynamicColor.onSurface().getArgb(scheme),
                                SURFACE_OPACITY_BUTTON_DISABLED
                            )
                    )

                val contentColorList =
                    buildColorStateList(
                        android.R.attr.state_enabled to dynamicColor.onPrimary().getArgb(scheme),
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                dynamicColor.onSurface().getArgb(scheme),
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
                        android.R.attr.state_enabled to dynamicColor.secondaryContainer().getArgb(scheme),
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                dynamicColor.onSurface().getArgb(scheme),
                                SURFACE_OPACITY_BUTTON_DISABLED
                            ),
                        -android.R.attr.state_hovered to dynamicColor.onSecondaryContainer().getArgb(scheme),
                        -android.R.attr.state_focused to dynamicColor.onSecondaryContainer().getArgb(scheme),
                        -android.R.attr.state_pressed to dynamicColor.onSecondaryContainer().getArgb(scheme)
                    )

                val contentColorList =
                    buildColorStateList(
                        android.R.attr.state_enabled to dynamicColor.onSecondaryContainer().getArgb(scheme),
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                dynamicColor.onSurface().getArgb(scheme),
                                ON_SURFACE_OPACITY_BUTTON_DISABLED
                            ),
                        -android.R.attr.state_hovered to dynamicColor.onSecondaryContainer().getArgb(scheme),
                        -android.R.attr.state_focused to dynamicColor.onSecondaryContainer().getArgb(scheme),
                        -android.R.attr.state_pressed to dynamicColor.onSecondaryContainer().getArgb(scheme)
                    )
                button.setTextColor(contentColorList)
                button.iconTint = contentColorList
            }
        }

        fun colorMaterialButtonPrimaryOutlined(button: MaterialButton) {
            withScheme(button) { scheme ->
                val contentColorList =
                    buildColorStateList(
                        android.R.attr.state_enabled to dynamicColor.primary().getArgb(scheme),
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                dynamicColor.onSurface().getArgb(scheme),
                                ON_SURFACE_OPACITY_BUTTON_DISABLED
                            )
                    )

                button.setTextColor(contentColorList)
                button.iconTint = contentColorList
                button.strokeColor =
                    buildColorStateList(
                        android.R.attr.state_enabled to dynamicColor.outline().getArgb(scheme),
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                dynamicColor.onSurface().getArgb(scheme),
                                ON_SURFACE_OPACITY_BUTTON_OUTLINE_DISABLED
                            ),
                        -android.R.attr.state_hovered to dynamicColor.outline().getArgb(scheme),
                        -android.R.attr.state_focused to dynamicColor.primary().getArgb(scheme),
                        -android.R.attr.state_pressed to dynamicColor.outline().getArgb(scheme)
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
                        android.R.attr.state_enabled to dynamicColor.primary().getArgb(scheme),
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                dynamicColor.onSurface().getArgb(scheme),
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
                        android.R.attr.state_enabled to dynamicColor.onPrimary().getArgb(scheme),
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                dynamicColor.surface().getArgb(scheme),
                                SURFACE_OPACITY_BUTTON_DISABLED
                            ),
                        -android.R.attr.state_hovered to dynamicColor.onPrimary().getArgb(scheme),
                        -android.R.attr.state_focused to dynamicColor.onPrimary().getArgb(scheme),
                        -android.R.attr.state_pressed to dynamicColor.onPrimary().getArgb(scheme)
                    )

                val contentColorList =
                    buildColorStateList(
                        android.R.attr.state_enabled to dynamicColor.primary().getArgb(scheme),
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                dynamicColor.primary().getArgb(scheme),
                                ON_SURFACE_OPACITY_BUTTON_DISABLED
                            ),
                        -android.R.attr.state_hovered to dynamicColor.primary().getArgb(scheme),
                        -android.R.attr.state_focused to dynamicColor.primary().getArgb(scheme),
                        -android.R.attr.state_pressed to dynamicColor.primary().getArgb(scheme)
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
                        android.R.attr.state_enabled to dynamicColor.onPrimary().getArgb(scheme),
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                dynamicColor.onPrimary().getArgb(scheme),
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
                toolbar.setBackgroundColor(dynamicColor.surface().getArgb(scheme))
                toolbar.setNavigationIconTint(dynamicColor.onSurface().getArgb(scheme))
                toolbar.setTitleTextColor(dynamicColor.onSurface().getArgb(scheme))
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
                colorProgressBar(progressIndicator, dynamicColor.primary().getArgb(scheme))
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
                colorProgressBar(progressIndicator, dynamicColor.primary().getArgb(scheme))
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
                val errorColor = dynamicColor.surfaceVariant().getArgb(scheme)

                val errorColorStateList =
                    buildColorStateList(
                        -android.R.attr.state_focused to errorColor,
                        android.R.attr.state_focused to errorColor
                    )

                val coloredColorStateList =
                    buildColorStateList(
                        -android.R.attr.state_focused to dynamicColor.outline().getArgb(scheme),
                        android.R.attr.state_focused to dynamicColor.primary().getArgb(scheme)
                    )

                textInputLayout.setBoxStrokeColorStateList(coloredColorStateList)
                textInputLayout.setErrorIconTintList(errorColorStateList)
                textInputLayout.setErrorTextColor(errorColorStateList)
                textInputLayout.boxStrokeErrorColor = errorColorStateList
                textInputLayout.defaultHintTextColor = coloredColorStateList

                textInputLayout.editText?.highlightColor = dynamicColor.primary().getArgb(scheme)
            }
        }

        fun colorTextInputLayout(
            textInputLayout: TextInputLayout,
            colorRole: ColorRole
        ) {
            withScheme(textInputLayout) { scheme ->
                val errorColor = dynamicColor.error().getArgb(scheme)

                val errorColorStateList =
                    buildColorStateList(
                        -android.R.attr.state_focused to errorColor,
                        android.R.attr.state_focused to errorColor
                    )

                val coloredColorStateList =
                    buildColorStateList(
                        -android.R.attr.state_focused to dynamicColor.outline().getArgb(scheme),
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
                tabLayout.setBackgroundColor(dynamicColor.surface().getArgb(scheme))
                colorTabLayout(tabLayout, scheme)
            }
        }

        fun colorBottomSheetBackground(
            bottomSheet: View,
            colorRole: ColorRole = ColorRole.SURFACE_CONTAINER_LOW
        ) {
            withScheme(bottomSheet) { scheme ->
                bottomSheet.setBackgroundColor(colorRole.select(scheme))
            }
        }

        fun colorBottomSheetDragHandle(
            bottomSheetDragHandleView: BottomSheetDragHandleView,
            colorRole: ColorRole = ColorRole.ON_SURFACE_VARIANT
        ) {
            withScheme(bottomSheetDragHandleView) { scheme ->
                bottomSheetDragHandleView.setColorFilter(colorRole.select(scheme))
            }
        }

        fun colorTabLayout(
            tabLayout: TabLayout,
            scheme: DynamicScheme
        ) {
            tabLayout.setSelectedTabIndicatorColor(dynamicColor.primary().getArgb(scheme))
            val tabContentColors =
                buildColorStateList(
                    android.R.attr.state_selected to dynamicColor.primary().getArgb(scheme),
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
                        android.R.attr.state_checked to dynamicColor.primary().getArgb(scheme),
                        -android.R.attr.state_checked to dynamicColor.outline().getArgb(scheme)
                    )

                materialCheckBox.buttonIconTintList =
                    buildColorStateList(
                        android.R.attr.state_checked to dynamicColor.onPrimary().getArgb(scheme),
                        -android.R.attr.state_checked to android.R.color.transparent
                    )
            }
        }

        fun colorMaterialSwitch(materialSwitch: MaterialSwitch) {
            withScheme(materialSwitch.context) { scheme ->
                materialSwitch.thumbTintList =
                    buildColorStateList(
                        android.R.attr.state_checked to dynamicColor.onPrimary().getArgb(scheme),
                        -android.R.attr.state_checked to dynamicColor.outline().getArgb(scheme)
                    )

                materialSwitch.trackTintList =
                    buildColorStateList(
                        android.R.attr.state_checked to dynamicColor.primary().getArgb(scheme),
                        // XXX: specs use surfaceContainerHighest
                        -android.R.attr.state_checked to dynamicColor.surface().getArgb(scheme)
                    )

                materialSwitch.trackDecorationTintList =
                    buildColorStateList(
                        android.R.attr.state_checked to android.R.color.transparent,
                        -android.R.attr.state_checked to dynamicColor.outline().getArgb(scheme)
                    )
            }
        }

        fun colorChipBackground(chip: Chip) {
            withScheme(chip) { scheme ->
                chip.chipBackgroundColor = ColorStateList.valueOf(dynamicColor.primary().getArgb(scheme))
                chip.setTextColor(dynamicColor.onPrimary().getArgb(scheme))
            }
        }

        fun colorChipDrawable(
            context: Context,
            chipDrawable: ChipDrawable
        ) {
            withScheme(context) { scheme ->
                chipDrawable.chipBackgroundColor = ColorStateList.valueOf(dynamicColor.primary().getArgb(scheme))
                chipDrawable.setTextColor(dynamicColor.onPrimary().getArgb(scheme))
            }
        }

        fun colorChipOutlined(
            chip: Chip,
            strokeWidth: Float
        ) {
            withScheme(chip) { scheme ->
                chip.chipBackgroundColor = ColorStateList.valueOf(Color.TRANSPARENT)
                chip.chipStrokeWidth = strokeWidth
                chip.chipStrokeColor = ColorStateList.valueOf(dynamicColor.primary().getArgb(scheme))
                chip.setTextColor(dynamicColor.primary().getArgb(scheme))
            }
        }

        fun themeSnackbar(snackbar: Snackbar) {
            withScheme(snackbar.context) { scheme ->
                snackbar.setBackgroundTint(dynamicColor.inverseSurface().getArgb(scheme))
                snackbar.setActionTextColor(dynamicColor.inversePrimary().getArgb(scheme))
                snackbar.setTextColor(dynamicColor.inverseOnSurface().getArgb(scheme))
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
                        android.R.attr.state_checked to dynamicColor.secondaryContainer().getArgb(scheme),
                        -android.R.attr.state_checked to dynamicColor.surface().getArgb(scheme),
                        android.R.attr.state_focused to dynamicColor.secondaryContainer().getArgb(scheme),
                        android.R.attr.state_hovered to dynamicColor.secondaryContainer().getArgb(scheme),
                        android.R.attr.state_pressed to dynamicColor.secondaryContainer().getArgb(scheme)
                    )

                val iconColors =
                    buildColorStateList(
                        android.R.attr.state_checked to dynamicColor.onSecondaryContainer().getArgb(scheme),
                        -android.R.attr.state_checked to dynamicColor.onSurfaceVariant().getArgb(scheme),
                        android.R.attr.state_focused to dynamicColor.onSecondaryContainer().getArgb(scheme),
                        android.R.attr.state_hovered to dynamicColor.onSecondaryContainer().getArgb(scheme),
                        android.R.attr.state_pressed to dynamicColor.onSecondaryContainer().getArgb(scheme)
                    )

                val textColors =
                    buildColorStateList(
                        android.R.attr.state_checked to dynamicColor.onSecondaryContainer().getArgb(scheme),
                        -android.R.attr.state_checked to dynamicColor.onSurfaceVariant().getArgb(scheme),
                        android.R.attr.state_hovered to dynamicColor.onSecondaryContainer().getArgb(scheme),
                        android.R.attr.state_focused to dynamicColor.onSecondaryContainer().getArgb(scheme),
                        android.R.attr.state_pressed to dynamicColor.onSecondaryContainer().getArgb(scheme)
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
                        android.R.attr.state_enabled to dynamicColor.primary().getArgb(scheme),
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                dynamicColor.onSurface().getArgb(scheme),
                                ON_SURFACE_OPACITY_BUTTON_DISABLED
                            ),
                        android.R.attr.state_focused to dynamicColor.primary().getArgb(scheme),
                        android.R.attr.state_hovered to dynamicColor.primary().getArgb(scheme),
                        android.R.attr.state_pressed to dynamicColor.primary().getArgb(scheme)
                    )

                val textColors =
                    buildColorStateList(
                        android.R.attr.state_enabled to dynamicColor.onSurface().getArgb(scheme),
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                dynamicColor.onSurface().getArgb(scheme),
                                ON_SURFACE_OPACITY_BUTTON_DISABLED
                            ),
                        android.R.attr.state_hovered to dynamicColor.onSurface().getArgb(scheme),
                        android.R.attr.state_focused to dynamicColor.onSurface().getArgb(scheme),
                        android.R.attr.state_pressed to dynamicColor.onSurface().getArgb(scheme)
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
                        android.R.attr.state_enabled to dynamicColor.onSurfaceVariant().getArgb(scheme),
                        -android.R.attr.state_enabled to
                            colorUtil.adjustOpacity(
                                dynamicColor.onSurface().getArgb(scheme),
                                ON_SURFACE_OPACITY_BUTTON_DISABLED
                            ),
                        android.R.attr.state_focused to dynamicColor.onSurfaceVariant().getArgb(scheme),
                        android.R.attr.state_hovered to dynamicColor.onSurfaceVariant().getArgb(scheme),
                        android.R.attr.state_pressed to dynamicColor.onSurfaceVariant().getArgb(scheme)
                    )

                chip.chipStrokeColor = chipOutlineFilterColorList(scheme)
                chip.setTextColor(chipSuggestionInputTextColorList(scheme))
                chip.chipIconTint = iconColors
            }
        }

        private fun chipOutlineColorList(scheme: DynamicScheme) =
            buildColorStateList(
                android.R.attr.state_enabled to dynamicColor.outlineVariant().getArgb(scheme),
                -android.R.attr.state_enabled to
                    colorUtil.adjustOpacity(
                        dynamicColor.onSurface().getArgb(scheme),
                        ON_SURFACE_OPACITY_BUTTON_OUTLINE_DISABLED
                    ),
                android.R.attr.state_hovered to dynamicColor.outlineVariant().getArgb(scheme),
                android.R.attr.state_focused to dynamicColor.onSurfaceVariant().getArgb(scheme),
                android.R.attr.state_pressed to dynamicColor.outlineVariant().getArgb(scheme)
            )

        private fun chipOutlineFilterColorList(scheme: DynamicScheme) =
            buildColorStateList(
                android.R.attr.state_checked to dynamicColor.secondaryContainer().getArgb(scheme),
                -android.R.attr.state_checked to dynamicColor.outlineVariant().getArgb(scheme)
            )

        private fun chipSuggestionInputTextColorList(scheme: DynamicScheme) =
            buildColorStateList(
                android.R.attr.state_enabled to dynamicColor.onSurfaceVariant().getArgb(scheme),
                -android.R.attr.state_enabled to
                    colorUtil.adjustOpacity(
                        dynamicColor.onSurface().getArgb(scheme),
                        ON_SURFACE_OPACITY_BUTTON_DISABLED
                    ),
                android.R.attr.state_hovered to dynamicColor.onSurfaceVariant().getArgb(scheme),
                android.R.attr.state_focused to dynamicColor.onSurfaceVariant().getArgb(scheme),
                android.R.attr.state_pressed to dynamicColor.onSurfaceVariant().getArgb(scheme)
            )

        private fun rippleColor(scheme: DynamicScheme) =
            buildColorStateList(
                android.R.attr.state_pressed to
                    colorUtil.adjustOpacity(
                        dynamicColor.primary().getArgb(scheme),
                        SURFACE_OPACITY_BUTTON_DISABLED
                    )
            )

        companion object {
            private const val SURFACE_OPACITY_BUTTON_DISABLED: Float = 0.12f
            private const val ON_SURFACE_OPACITY_BUTTON_OUTLINE_DISABLED: Float = 0.12f
            private const val ON_SURFACE_OPACITY_BUTTON_DISABLED: Float = 0.38f
        }
    }
