package com.lswmobile.app.utils

import com.lswmobile.app.network.model.ErrorResponse
import kotlinx.serialization.json.Json

object ErrorUtils {
    
    private val json = Json { 
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    
    /**
     * Extracts user-friendly error message from exception
     * Handles backend error format: {"success":false,"message":"error message"}
     */
    fun extractErrorMessage(exception: Exception, fallbackMessage: String = "An unexpected error occurred"): String {
        val exceptionMessage = exception.message ?: return fallbackMessage
        
        println("ErrorUtils: Raw exception message: $exceptionMessage")
        
        return try {
            // Try to parse as backend error response
            val errorResponse = json.decodeFromString<ErrorResponse>(exceptionMessage)
            val extractedMessage = if (!errorResponse.success && errorResponse.message.isNotBlank()) {
                errorResponse.message
            } else {
                fallbackMessage
            }
            println("ErrorUtils: Extracted message via JSON parsing: $extractedMessage")
            extractedMessage
        } catch (e: Exception) {
            println("ErrorUtils: JSON parsing failed: ${e.message}")
            // If parsing fails, check if it's a simple message
            if (exceptionMessage.startsWith("{") && exceptionMessage.contains("\"message\"")) {
                // Try to extract message manually if JSON parsing fails
                val manualExtracted = extractMessageFromJson(exceptionMessage) ?: fallbackMessage
                println("ErrorUtils: Extracted message via manual parsing: $manualExtracted")
                manualExtracted
            } else {
                // Return the original message if it's not JSON
                println("ErrorUtils: Using original message: $exceptionMessage")
                exceptionMessage
            }
        }
    }
    
    /**
     * Manual extraction of message from JSON string as fallback
     */
    private fun extractMessageFromJson(jsonString: String): String? {
        return try {
            val messageStart = jsonString.indexOf("\"message\":\"") + 11
            if (messageStart > 10) {
                val messageEnd = jsonString.indexOf("\"", messageStart)
                if (messageEnd > messageStart) {
                    jsonString.substring(messageStart, messageEnd)
                        .replace("\\n", "\n") // Handle escaped newlines
                        .replace("\\\"", "\"") // Handle escaped quotes
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
