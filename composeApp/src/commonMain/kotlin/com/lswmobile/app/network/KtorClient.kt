package com.lswmobile.app.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Ktor HTTP client for making API requests
 */
class KtorClient(
    private val tokenProvider: TokenProvider, 
    private val baseUrl: String,
    private val enableLogging: Boolean = true
) {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { 
                prettyPrint = false
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
        
        if (enableLogging) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
        
        install(Auth) {
            bearer {
                loadTokens {
                    val accessToken = tokenProvider.getAccessToken()
                    val refreshToken = tokenProvider.getRefreshToken()
                    if (accessToken != null) {
                        BearerTokens(accessToken, refreshToken ?: "")
                    } else {
                        null
                    }
                }
                
                refreshTokens {
                    val refreshToken = tokenProvider.getRefreshToken() ?: return@refreshTokens null
                    
                    try {
                        val tokenResponse = tokenProvider.refreshTokens(refreshToken)
                        if (tokenResponse != null) {
                            val (newAccessToken, newRefreshToken) = tokenResponse
                            tokenProvider.saveTokens(newAccessToken, newRefreshToken)
                            BearerTokens(newAccessToken, newRefreshToken)
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        tokenProvider.clearTokens()
                        null
                    }
                }
            }
        }
        
        defaultRequest {
            url(baseUrl)
            contentType(ContentType.Application.Json)
        }
        
        // Handle client-side errors (e.g., timeout, network issues)
        HttpResponseValidator {
            validateResponse { response ->
                val statusCode = response.status.value
                
                if (statusCode !in 200..299) {
                    // Try to extract error message
                    val errorBody = runCatching { response.bodyAsText() }.getOrNull()
                    when (statusCode) {
                        401 -> throw UnauthorizedException(errorBody ?: "Unauthorized")
                        403 -> throw ForbiddenException(errorBody ?: "Access denied")
                        404 -> throw NotFoundException(errorBody ?: "Resource not found")
                        in 400..499 -> throw ClientRequestException(response, errorBody ?: "Client error")
                        in 500..599 -> throw ServerResponseException(response, errorBody ?: "Server error")
                    }
                }
            }
        }
    }
}

// Custom exceptions
class UnauthorizedException(message: String) : Exception(message)
class ForbiddenException(message: String) : Exception(message)
class NotFoundException(message: String) : Exception(message)
