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

package com.nextcloud.android.common.ui.util

import android.content.res.ColorStateList

/**
 * First element of each pair is @AttRes (can be negated) and second one is @ColorInt
 */
fun buildColorStateList(vararg pairs: Pair<Int, Int>): ColorStateList {
    val stateArray = pairs.map { intArrayOf(it.first) }.toTypedArray()
    val colorArray = pairs.map { it.second }.toIntArray()
    return ColorStateList(stateArray, colorArray)
}
