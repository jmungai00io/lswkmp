package com.lswmobile.app.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import platform.Foundation.NSHomeDirectory

/**
 * Factory for creating SQLDelight database drivers on iOS
 */
actual class DatabaseDriverFactory {
    
    actual fun createDriver(): SqlDriver {
        val databasePath = NSHomeDirectory() + "/Documents/cart.db"
        return NativeSqliteDriver(
            schema = CartDatabase.Schema,
            name = databasePath
        )
    }
} 