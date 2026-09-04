/*
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */

@file:Suppress("FunctionNaming")

package com.nextcloud.android.common.ui.share.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nextcloud.android.common.ui.share.model.ui.ShareItemType

private const val SHIMMER_ROW_COUNT = 6
private const val ITEM_WIDTH_FRACTION = 0.9f
private const val CONTAINER_ALPHA = 0.5f
private const val HEADLINE_WIDTH_FRACTION = 0.5f
private const val CHIP_CORNER_PERCENT = 50
private const val SHIMMER_MIN_ALPHA = 0.12f
private const val SHIMMER_MAX_ALPHA = 0.28f
private const val SHIMMER_ANIMATION_DURATION_MILLIS = 900

private val FIRST_ITEM_TOP_SPACING = 16.dp
private val ITEM_SPACING = 2.dp
private val ROW_PADDING = 16.dp
private val CONTENT_SPACING = 16.dp
private val LEADING_ICON_SIZE = 24.dp
private val TRAILING_ICON_SIZE = 48.dp
private val HEADLINE_HEIGHT = 16.dp
private val LINE_SPACING = 8.dp
private val CHIP_WIDTH = 90.dp
private val CHIP_HEIGHT = 20.dp
private val BAR_CORNER_RADIUS = 4.dp

@Composable
fun ShareListShimmer(modifier: Modifier = Modifier) {
    val alpha = rememberShimmerAlpha()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        userScrollEnabled = false
    ) {
        items(SHIMMER_ROW_COUNT) { index ->
            Spacer(modifier = Modifier.height(if (index == 0) FIRST_ITEM_TOP_SPACING else ITEM_SPACING))
            ShareListItemShimmer(
                type = ShareItemType.type(index, SHIMMER_ROW_COUNT - 1),
                alpha = alpha
            )
        }
    }
}

@Composable
private fun rememberShimmerAlpha(): Float {
    val transition = rememberInfiniteTransition(label = "shareListShimmer")
    val alpha by transition.animateFloat(
        initialValue = SHIMMER_MIN_ALPHA,
        targetValue = SHIMMER_MAX_ALPHA,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = SHIMMER_ANIMATION_DURATION_MILLIS, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shareListShimmerAlpha"
    )
    return alpha
}

@Composable
private fun ShareListItemShimmer(type: ShareItemType, alpha: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth(ITEM_WIDTH_FRACTION)
            .clip(type.getShape())
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = CONTAINER_ALPHA))
            .padding(ROW_PADDING)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ShimmerBox(
                modifier = Modifier
                    .size(LEADING_ICON_SIZE)
                    .clip(CircleShape),
                alpha = alpha
            )

            Spacer(modifier = Modifier.width(CONTENT_SPACING))

            Column(modifier = Modifier.weight(1f)) {
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(HEADLINE_WIDTH_FRACTION)
                        .height(HEADLINE_HEIGHT)
                        .clip(RoundedCornerShape(BAR_CORNER_RADIUS)),
                    alpha = alpha
                )

                Spacer(modifier = Modifier.height(LINE_SPACING))

                ShimmerBox(
                    modifier = Modifier
                        .width(CHIP_WIDTH)
                        .height(CHIP_HEIGHT)
                        .clip(RoundedCornerShape(percent = CHIP_CORNER_PERCENT)),
                    alpha = alpha
                )
            }

            Spacer(modifier = Modifier.width(CONTENT_SPACING))

            ShimmerBox(
                modifier = Modifier
                    .size(TRAILING_ICON_SIZE)
                    .clip(CircleShape),
                alpha = alpha
            )
        }
    }
}

@Composable
private fun ShimmerBox(modifier: Modifier, alpha: Float) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha))
    )
}
