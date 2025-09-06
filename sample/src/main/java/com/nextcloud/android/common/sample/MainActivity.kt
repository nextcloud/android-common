/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2022-2023 Nextcloud GmbH and Nextcloud contributors
 * SPDX-FileCopyrightText: 2023 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-FileCopyrightText: 2023 Stefan Niedermann <info@niedermann.it>
 * SPDX-License-Identifier: MIT
 */
package com.nextcloud.android.common.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nextcloud.android.common.sample.databinding.ActivityMainBinding
import com.nextcloud.android.common.ui.color.ColorUtil
import com.nextcloud.android.common.ui.theme.MaterialSchemes
import com.nextcloud.android.common.ui.theme.utils.AndroidViewThemeUtils
import com.nextcloud.android.common.ui.theme.utils.ColorRole
import com.nextcloud.android.common.ui.theme.utils.DialogViewThemeUtils
import com.nextcloud.android.common.ui.theme.utils.MaterialViewThemeUtils

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // Color should be fetched from the server capabilities or another proper source
        binding.btn.setOnClickListener { _ ->
            try {
                mainViewModel.color.value = "#${binding.color.text}".toColorInt()
            } catch (_: java.lang.IllegalArgumentException) {
                Toast
                    .makeText(
                        this,
                        "#${binding.color.text} is not a valid color.",
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }

        binding.dialogBtn.setOnClickListener { _ ->
            // launch MaterialDialog
            val builder =
                MaterialAlertDialogBuilder(this)
                    .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
                    .setNeutralButton(R.string.dismiss) { dialog, _ -> dialog.dismiss() }
                    .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                    .setTitle(R.string.dialog_title)
                    .setMessage(getString(R.string.dialog_message))
            val schemes = MaterialSchemes.Companion.fromColor("#${binding.color.text}".toColorInt())
            val colorUtil = ColorUtil(this)
            val dialogViewThemeUtils = DialogViewThemeUtils(schemes)
            val material = MaterialViewThemeUtils(schemes, colorUtil)

            dialogViewThemeUtils.colorMaterialAlertDialogBackground(this, builder)

            val dialog = builder.create()
            dialog.show()

            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE) as MaterialButton
            material.colorMaterialButtonPrimaryTonal(positiveButton)

            val neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL) as MaterialButton
            material.colorMaterialButtonPrimaryOutlined(neutralButton)

            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE) as MaterialButton
            material.colorMaterialButtonPrimaryBorderless(negativeButton)
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainViewModel.color.observe(this) { applyTheme(it) }
        applyTheme("#${binding.color.text}".toColorInt())
    }

    private fun applyTheme(color: Int) {
        // Define your MaterialSchemes and ColorUtil
        val schemes = MaterialSchemes.Companion.fromColor(color)
        val colorUtil = ColorUtil(this)

        // Use them to instantiate ThemUtils you need
        val platform = AndroidViewThemeUtils(schemes, colorUtil)
        val material = MaterialViewThemeUtils(schemes, colorUtil)
        // val androidx = AndroidXViewThemeUtils(schemes, platform)
        // val dialog = DialogViewThemeUtils(schemes)

        // Use the methods of the ThemeUtils to apply the actual theme.
        // For a consistent User Experience it is necessary to apply the theme to *every* UI element
        platform.colorViewBackground(binding.container, ColorRole.SURFACE)
        platform.colorTextView(binding.headlineLib, ColorRole.PRIMARY)
        platform.colorTextView(binding.headlineModuleUi, ColorRole.SECONDARY)
        platform.colorTextView(binding.toolbarTitle, ColorRole.ON_SURFACE)
        platform.themeStatusBar(this)
        material.themeToolbar(binding.toolbar)
        material.colorTextInputLayout(binding.colorTil)
        material.themeExtendedFAB(binding.btn)
        material.themeChipAssist(binding.assistChip)
        material.themeChipInput(binding.inputChip)
        material.themeChipSuggestion(binding.suggestionChip)
        material.themeChipFilter(binding.filterChip)
        material.colorMaterialButtonPrimaryFilled(binding.dialogBtn)
    }
}
