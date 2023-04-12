/*
 * Nextcloud Android Common Library
 *
 * Copyright (C) 2023 Nextcloud GmbH
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

package com.nextcloud.android.common.sample.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nextcloud.android.common.sample.R
import com.nextcloud.android.common.sample.databinding.FragmentUiBinding
import com.nextcloud.android.common.ui.color.ColorUtil
import com.nextcloud.android.common.ui.theme.MaterialSchemes
import com.nextcloud.android.common.ui.theme.utils.AndroidViewThemeUtils
import com.nextcloud.android.common.ui.theme.utils.AndroidXViewThemeUtils
import com.nextcloud.android.common.ui.theme.utils.DialogViewThemeUtils
import com.nextcloud.android.common.ui.theme.utils.MaterialViewThemeUtils


class UiFragment : Fragment() {

    private lateinit var binding: FragmentUiBinding;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val uiViewModel = ViewModelProvider(this)[UiViewModel::class.java]

        binding = FragmentUiBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // color should be fetched from the server capabilities
        binding.color.doOnTextChanged { text, _, _, _ ->
            try {
                uiViewModel.color.value = Color.parseColor("#$text")
            } catch (_: java.lang.IllegalArgumentException) {
                // Not a valid color
            }
        }
        uiViewModel.color.observe(viewLifecycleOwner) { applyTheme(it) }

        return root
    }

    private fun applyTheme(color: Int) {
        // Define your MaterialSchemes and ColorUtil
        val schemes = MaterialSchemes.Companion.fromColor(color)
        val colorUtil = ColorUtil(requireContext())

        // Use them to instantiate ThemUtils you need
        val platform = AndroidViewThemeUtils(schemes, colorUtil)
        val material = MaterialViewThemeUtils(schemes, colorUtil)
        val androidx = AndroidXViewThemeUtils(schemes, platform)
        val dialog = DialogViewThemeUtils(schemes)

        // Use the methods of the ThemeUtils to apply the actual theme:
        platform.themeStatusBar(requireActivity())
        platform.colorBottomNavigationView(requireActivity().findViewById(R.id.nav_view))
        material.colorTextInputLayout(binding.colorTil)
    }
}