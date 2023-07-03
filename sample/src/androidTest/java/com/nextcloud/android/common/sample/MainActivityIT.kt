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

package com.nextcloud.android.common.sample

import android.graphics.Color
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test

class MainActivityIT {
    @get:Rule
    val testActivityRule = IntentsTestRule(MainActivity::class.java, true, false)

    @Test
    fun highlight() {
        val activity = testActivityRule.launchActivity(null)

        testActivityRule.runOnUiThread {
            activity.applyTheme(Color.parseColor("#0082c9"))
            
            val platform = activity.platform
            val binding = activity.binding
            
            binding.sampleTextView.text = "11111"
            platform.highlightText(binding.sampleTextView, binding.sampleTextView.text as String, "1")
            
            Thread.sleep(2000)

            binding.sampleTextView.text = "LLorem ipsum dolor sit amet."
            platform.highlightText(binding.sampleTextView, binding.sampleTextView.text.toString() , "L")
            
            Thread.sleep(2000)
        }
    }
}
