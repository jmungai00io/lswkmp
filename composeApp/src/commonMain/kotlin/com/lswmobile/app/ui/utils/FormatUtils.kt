package com.lswmobile.app.ui.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Format currency amount
 */
fun formatCurrency(amount: Double): String {
    val cents = (amount * 100).toInt()
    val rands = cents / 100
    val centsRemainder = cents % 100
    return "R $rands.${centsRemainder.toString().padStart(2, '0')}"
}

/**
 * Format date from milliseconds
 */
fun formatDate(milliseconds: Long): String {
    val instant = Instant.fromEpochMilliseconds(milliseconds)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    
    val monthNames = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )
    
    val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
    val month = monthNames[localDateTime.monthNumber - 1]
    val year = localDateTime.year
    
    return "$day $month $year"
} 