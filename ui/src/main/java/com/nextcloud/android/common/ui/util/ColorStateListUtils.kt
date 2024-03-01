/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2022-2023 Nextcloud GmbH and Nextcloud contributors
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-License-Identifier: MIT
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
