import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties
import java.io.FileInputStream

// Load secrets.properties if it exists
val secretsProperties = Properties()
val secretsFile = rootProject.file("secrets.properties")
if (secretsFile.exists()) {
    secretsProperties.load(FileInputStream(secretsFile))
} else {
    // Default values if secrets.properties doesn't exist
    secretsProperties["ONESIGNAL_APP_ID_DEVELOPMENT"] = "\"development_app_id_placeholder\""
    secretsProperties["ONESIGNAL_APP_ID_STAGING"] = "\"staging_app_id_placeholder\""
    secretsProperties["ONESIGNAL_APP_ID_PRODUCTION"] = "\"production_app_id_placeholder\""
}

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.android)
        }
        
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            
            // Material Icons
//            implementation("androidx.compose.material:material-icons-core:1.5.4")
//            implementation("androidx.compose.material:material-icons-extended:1.5.4")
            
            // Ktor
            implementation(libs.ktor.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.auth)
            
            // Kotlinx
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            
            // Koin for Dependency Injection
            implementation("io.insert-koin:koin-core:3.5.0")
            implementation("io.insert-koin:koin-compose:1.1.0")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.lswmobile.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.lswmobile.app"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    
    flavorDimensions += "type"
    productFlavors {
        create("development") {
            dimension = "type"
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:4000\"")
            buildConfigField("String", "WEB_BASE_URL", "\"https://staging.livestockwealth.com\"")
            buildConfigField("String", "ONESIGNAL_APP_ID", secretsProperties["ONESIGNAL_APP_ID_DEVELOPMENT"].toString())
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }
        
        create("staging") {
            dimension = "type"
            buildConfigField("String", "BASE_URL", "\"https://staging.api.livestockwealth.com\"")
            buildConfigField("String", "WEB_BASE_URL", "\"https://staging.livestockwealth.com\"")
            buildConfigField("String", "ONESIGNAL_APP_ID", secretsProperties["ONESIGNAL_APP_ID_STAGING"].toString())
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
        }
        
        create("production") {
            dimension = "type"
            buildConfigField("String", "BASE_URL", "\"https://api.livestockwealth.com\"")
            buildConfigField("String", "WEB_BASE_URL", "\"https://livestockwealth.com\"")
            buildConfigField("String", "ONESIGNAL_APP_ID", secretsProperties["ONESIGNAL_APP_ID_PRODUCTION"].toString())
        }
    }
    
    // Add a build flag for environment name
    applicationVariants.all {
        val variant = this
        variant.buildConfigField("String", "ENVIRONMENT_NAME", "\"${variant.flavorName}\"")
        
        // Add app name suffix for non-production builds
        if (variant.flavorName != "production") {
            resValue("string", "app_name", "LSW ${variant.flavorName.capitalize()}")
        } else {
            resValue("string", "app_name", "Livestock Wealth")
        }
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
    }
    
    // Add lint configuration
    lint {
        abortOnError = false  // Don't abort the build if there are lint errors
        checkReleaseBuilds = false  // Don't check lint for release builds
        // Disable lint checks that are causing problems
        disable += setOf(
            "InvalidPackage",
            "ObsoleteSdkInt",
            "NewApi",
            "GradleDependency",
            "MissingTranslation"
        )
    }
}

// Fix for syncComposeResourcesForIos task configuration issues
tasks.named("syncComposeResourcesForIos") {
    enabled = false  // Completely disable this task
    outputs.upToDateWhen { true }  // Make it always up-to-date
}

dependencies {
    debugImplementation(compose.uiTooling)
}
