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

/**
 * Format ID type from snake_case to Title Case
 */
fun formatIdType(idType: String): String {
    return when (idType.uppercase()) {
        "PASSPORT" -> "Passport"
        "NATIONAL_ID" -> "National ID"
        else -> idType.replace("_", " ").split(" ").joinToString(" ") { word ->
            word.lowercase().capitalize()
        }
    }
}

/**
 * Format name to Title Case
 */
fun formatName(name: String): String {
    return name.split(" ").joinToString(" ") { word ->
        word.lowercase().capitalize()
    }
}

/**
 * Validate international phone number
 * Supports formats like: +1234567890, +1-234-567-8900, +1 (234) 567-8900, etc.
 */
fun isValidPhoneNumber(phone: String): Boolean {
    // Remove spaces, dashes, parentheses, and dots
    val cleanedPhone = phone.replace(Regex("[\\s\\-\\(\\)\\.]"), "")
    
    // International phone number regex
    // Matches: +[country code][number] where country code is 1-3 digits and number is 7-15 digits
    val phoneRegex = Regex("^\\+[1-9]\\d{1,3}\\d{7,15}$")
    
    return phoneRegex.matches(cleanedPhone)
}

/**
 * Validate email address
 */
fun isValidEmail(email: String): Boolean {
    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    return emailRegex.matches(email)
}