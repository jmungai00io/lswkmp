package com.lswmobile.app.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

/**
 * Factory for creating SQLDelight database drivers on Android
 */
actual class DatabaseDriverFactory {
    
    actual fun createDriver(): SqlDriver {
        // We'll need to get the context from the application
        // For now, this will need to be provided through dependency injection
        val context = getApplicationContext()
        return AndroidSqliteDriver(
            schema = CartDatabase.Schema,
            context = context,
            name = "cart.db"
        )
    }
    
    private fun getApplicationContext(): Context {
        // This should be provided by the Android application
        // For now, we'll need to inject this or use a global context
        throw NotImplementedError("Application context needs to be provided through dependency injection")
    }
}

 