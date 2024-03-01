/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2022-2023 Nextcloud GmbH and Nextcloud contributors
 * SPDX-FileCopyrightText: 2022-2023 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-License-Identifier: MIT
 */
package com.nextcloud.android.common.ui.theme.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.shape.MaterialShapeDrawable
import com.nextcloud.android.common.ui.R
import com.nextcloud.android.common.ui.theme.MaterialSchemes
import com.nextcloud.android.common.ui.theme.ViewThemeUtilsBase
import javax.inject.Inject

/**
 * View theme utils for dialogs
 */
class DialogViewThemeUtils
    @Inject
    constructor(schemes: MaterialSchemes) :
    ViewThemeUtilsBase(schemes) {
        fun colorMaterialAlertDialogBackground(
            context: Context,
            dialogBuilder: MaterialAlertDialogBuilder
        ) {
            withScheme(dialogBuilder.context) { scheme ->
                val materialShapeDrawable =
                    MaterialShapeDrawable(
                        context,
                        null,
                        com.google.android.material.R.attr.alertDialogStyle,
                        com.google.android.material.R.style.MaterialAlertDialog_MaterialComponents
                    )
                materialShapeDrawable.initializeElevationOverlay(context)
                materialShapeDrawable.fillColor = ColorStateList.valueOf(scheme.surface)

                // dialogCornerRadius first appeared in Android Pie
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val radius = context.resources.getDimension(R.dimen.dialogBorderRadius)
                    materialShapeDrawable.setCornerSize(radius)
                }

                dialogBuilder.background = materialShapeDrawable
            }
        }

        fun colorDialogMenuText(button: MaterialButton) {
            withScheme(button) { scheme ->
                button.setTextColor(scheme.onSurface)
                button.iconTint = ColorStateList.valueOf(scheme.onSurface)
            }
        }

        fun colorDialogHeadline(textView: TextView) {
            withScheme(textView) { scheme ->
                textView.setTextColor(scheme.onSurface)
            }
        }

        fun colorDialogSupportingText(textView: TextView) {
            withScheme(textView) { scheme ->
                textView.setTextColor(scheme.onSurfaceVariant)
            }
        }

        fun colorDialogIcon(icon: ImageView) {
            withScheme(icon) { scheme ->
                icon.setColorFilter(scheme.secondary)
            }
        }

        fun colorMaterialAlertDialogIcon(
            context: Context,
            drawableId: Int
        ): Drawable {
            val drawable = AppCompatResources.getDrawable(context, drawableId)!!
            withScheme(context) { scheme ->
                DrawableCompat.setTint(drawable, scheme.secondary)
            }
            return drawable
        }
    }
