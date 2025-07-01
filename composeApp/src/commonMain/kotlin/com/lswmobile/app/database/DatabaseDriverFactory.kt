package com.lswmobile.app.database

import app.cash.sqldelight.db.SqlDriver

/**
 * Common interface for database driver factory
 * This will be implemented by platform-specific versions
 */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
} 