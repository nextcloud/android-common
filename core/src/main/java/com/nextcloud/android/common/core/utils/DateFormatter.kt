/**
 * Nextcloud Android Common Library
 *
 * SPDX-FileCopyrightText: 2025 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: MIT
 */
package com.nextcloud.android.common.core.utils

import android.content.Context
import android.os.Build
import android.text.format.DateFormat
import com.nextcloud.android.common.core.R
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.text.toInt

/**
 * Helper implementation for date formatting.
 */
class DateFormatter(
    private val context: Context
) {
    private val sdfDays: SimpleDateFormat
    private val sdfMonths: SimpleDateFormat
    private val sdfYears: SimpleDateFormat

    /**
     * constructor.
     */
    init {
        val locale =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                this.context.resources.configuration
                    .getLocales()
                    .get(0)
            } else {
                this.context.resources.configuration.locale
            }
        this.sdfDays = SimpleDateFormat(DateFormat.getBestDateTimePattern(locale, "EEE"), locale)
        this.sdfMonths =
            SimpleDateFormat(DateFormat.getBestDateTimePattern(locale, "MMM d"), locale)
        this.sdfYears =
            SimpleDateFormat(DateFormat.getBestDateTimePattern(locale, "MMM d, yyyy"), locale)
    }

    /**
     * Returns a conditionally relative date formated string.
     * For anything less than 1h hours a relative time in minutes will be returned,
     * for anything less than 24h hours a relative time in hours will be returned,
     * for anything less than 6 days the day of the week in short form,
     * for anything up to 364 days a day and moth string will be returned,
     * for anything more than 364 days from now a complete date will be returned.
     *
     * @param calendar to be formatted calendar
     * @return formatted date strings
     */
    fun getConditionallyRelativeFormattedTimeSpan(calendar: Calendar): String {
        val span = System.currentTimeMillis() - calendar.getTimeInMillis()
        return when {
            // less than 1m
            span < ONE_MINUTE_IN_MILLIS -> context.getString(R.string.date_formatting_now)
            // less than 1h
            span < ONE_HOUR_IN_MILLIS ->
                context.getString(
                    R.string.date_formatting_relative_minutes,
                    span / ONE_MINUTE_IN_MILLIS
                )
            // less than 1d
            span < ONE_DAY_IN_MILLIS -> {
                val hours: Int = span.toInt() / ONE_HOUR_IN_MILLIS
                context
                    .resources
                    .getQuantityString(R.plurals.date_formatting_relative_hours, hours, hours)
            }
            // less than 1w
            span <= SIX_DAYS_IN_MILLIS -> sdfDays.format(calendar.getTime())
            // less than 1y -> up to 364 days
            span <= YEAR_IN_MILLIS -> sdfMonths.format(calendar.getTime())
            // more than 1y -> more than 364 days
            else -> sdfYears.format(calendar.getTime())
        }
    }

    /**
     * Time constants for conditional formatting.
     */
    companion object {
        private const val ONE_MINUTE_IN_MILLIS: Int = 60000
        private const val ONE_HOUR_IN_MILLIS: Int = ONE_MINUTE_IN_MILLIS * 60
        private const val ONE_DAY_IN_MILLIS: Int = ONE_HOUR_IN_MILLIS * 24
        private const val SIX_DAYS_IN_MILLIS: Int = ONE_DAY_IN_MILLIS * 6
        private const val YEAR_IN_MILLIS: Long = ONE_DAY_IN_MILLIS * 364L
    }
}
