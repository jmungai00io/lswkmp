package com.lswmobile.app.ui.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Format a date to a readable string
 */
fun formatDate(date: String): String {
    return try {
        val dateTime = Instant.parse(date)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        
        val day = dateTime.date.dayOfMonth
        val month = getMonthName(dateTime.date.monthNumber)
        val year = dateTime.date.year
        
        "$day $month $year"
    } catch (e: Exception) {
        "Invalid date"
    }
}

/**
 * Get month name from month number
 */
fun getMonthName(month: Int): String {
    return when (month) {
        1 -> "Jan"
        2 -> "Feb"
        3 -> "Mar"
        4 -> "Apr"
        5 -> "May"
        6 -> "Jun"
        7 -> "Jul"
        8 -> "Aug"
        9 -> "Sep"
        10 -> "Oct"
        11 -> "Nov"
        12 -> "Dec"
        else -> "Unknown"
    }
}

/**
 * Extension function to capitalize a string
 */
fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}