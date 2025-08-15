package com.lswmobile.app.util

import kotlin.math.abs
import kotlin.math.round

/**
 * Cross-platform number formatting helpers (no java.text / String.format).
 */
object NumberFormatUtils {
    /**
     * Format a Double to a string with exactly two decimal places.
     * Works on all KMP targets by operating on rounded cents.
     */
    fun formatTwoDecimals(value: Double): String {
        val cents = round(value * 100.0).toLong()
        val sign = if (cents < 0) "-" else ""
        val absCents = abs(cents)
        val whole = absCents / 100
        val frac = (absCents % 100).toInt()
        val fracStr = if (frac < 10) "0$frac" else "$frac"
        return "$sign$whole.$fracStr"
    }

    /**
     * Format a currency amount prefixed with 'R' and two decimals.
     */
    fun formatCurrencyR(value: Double): String = "R" + formatTwoDecimals(value)
}
