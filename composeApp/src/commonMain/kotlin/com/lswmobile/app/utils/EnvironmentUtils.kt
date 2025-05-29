package com.lswmobile.app.utils

import com.lswmobile.app.config.AppConfigFactory

/**
 * Utility object for environment-related operations
 */
object EnvironmentUtils {
    private val config = AppConfigFactory.get()
    
    /**
     * Checks if the app is running in development environment
     */
    fun isDevelopment(): Boolean = config.isDevelopment
    
    /**
     * Checks if the app is running in staging environment
     */
    fun isStaging(): Boolean = config.environmentName == "staging"
    
    /**
     * Checks if the app is running in production environment
     */
    fun isProduction(): Boolean = config.environmentName == "production"
    
    /**
     * Gets the current environment name
     */
    fun getEnvironmentName(): String = config.environmentName
    
    /**
     * Executes code only in development environment
     */
    inline fun ifDevelopment(block: () -> Unit) {
        if (isDevelopment()) block()
    }
    
    /**
     * Executes code only in staging environment
     */
    inline fun ifStaging(block: () -> Unit) {
        if (isStaging()) block()
    }
    
    /**
     * Executes code only in production environment
     */
    inline fun ifProduction(block: () -> Unit) {
        if (isProduction()) block()
    }
    
    /**
     * Executes different code based on environment
     */
    inline fun <T> whenEnvironment(
        development: () -> T,
        staging: () -> T,
        production: () -> T
    ): T {
        return when {
            isDevelopment() -> development()
            isStaging() -> staging()
            else -> production()
        }
    }
}
