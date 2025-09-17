# Livestock Wealth Mobile App

A cross-platform mobile application built with Kotlin Multiplatform and Compose Multiplatform.

## Features

- Cross-platform authentication
- KYC document upload with camera integration
- Marketplace functionality
- Order management
- Wallet management
- Profile management

## KYC Camera Implementation

The KYC document upload feature has been implemented with a platform-specific camera interface that currently uses simulated camera data. Here's how it works:

### Current Implementation

1. **Platform Interface**: `CameraInterface` in `Platform.kt` defines the contract for camera operations
2. **Android Implementation**: `AndroidCameraInterface` in `Platform.android.kt` (currently simulates camera capture)
3. **iOS Implementation**: `IOSCameraInterface` in `Platform.ios.kt` (currently simulates camera capture)
4. **Integration**: The KYC screen uses `getCameraInterface()` to get the platform-specific implementation

### How to Implement Real Camera Functionality

#### Android (CameraX)

1. **Update AndroidCameraInterface.takePhoto()**:
   ```kotlin
   override fun takePhoto(
       documentType: String,
       onPhotoTaken: (ByteArray, String) -> Unit,
       onError: (String) -> Unit
   ) {
       // Launch camera activity with CameraX
       val imageCapture = ImageCapture.Builder()
           .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
           .build()
       
       // Set up camera provider and bind use cases
       // Capture image and convert to ByteArray
       // Call onPhotoTaken with the image data
   }
   ```

2. **Update permission handling**:
   ```kotlin
   override fun requestCameraPermission(
       onPermissionGranted: () -> Unit,
       onPermissionDenied: () -> Unit
   ) {
       // Use ActivityResultLauncher to request camera permission
       // Check result and call appropriate callback
   }
   ```

#### iOS (UIImagePickerController)

1. **Update IOSCameraInterface.takePhoto()**:
   ```kotlin
   override fun takePhoto(
       documentType: String,
       onPhotoTaken: (ByteArray, String) -> Unit,
       onError: (String) -> Unit
   ) {
       // Present UIImagePickerController with camera source
       // Handle delegate callbacks
       // Convert UIImage to ByteArray
       // Call onPhotoTaken with the image data
   }
   ```

2. **Update permission handling**:
   ```kotlin
   override fun requestCameraPermission(
       onPermissionGranted: () -> Unit,
       onPermissionDenied: () -> Unit
   ) {
       // Check AVAuthorizationStatus
       // Request permission if needed
       // Call appropriate callback
   }
   ```

### Current Simulated Data

The current implementation generates realistic JPEG images (100x100 pixels) that are valid JPEG files. This allows testing of the upload flow without requiring actual camera hardware.

### Dependencies

- **Android**: CameraX dependencies are already added to `build.gradle.kts`
- **iOS**: No additional dependencies needed (uses UIKit)

### Permissions

- **Android**: Camera permission is declared in `AndroidManifest.xml`
- **iOS**: Camera usage description should be added to `Info.plist`

## Development

### Prerequisites

- Kotlin 1.9.0+
- Android Studio Hedgehog or later
- Xcode 15.0+ (for iOS development)
- JDK 11+

### Building

```bash
# Build for Android
./gradlew assembleDebug

# Build for iOS
./gradlew linkReleaseFrameworkIosArm64
```

### Running

```bash
# Run on Android
./gradlew installDebug

# Run on iOS (requires Xcode)
# 1) Install CocoaPods dependencies and generate the workspace
#    (from the iosApp/ directory):
#
#    pod install
#
# 2) Open the generated workspace (NOT the .xcodeproj):
open iosApp/iosApp.xcworkspace
```

## Architecture

The app follows MVVM architecture with:
- **ViewModels**: Handle business logic and state management
- **Repositories**: Handle data operations
- **Network Layer**: Ktor client for API communication
- **UI Layer**: Compose Multiplatform for cross-platform UI

## Dependencies

- **Compose Multiplatform**: UI framework
- **Ktor**: HTTP client
- **Koin**: Dependency injection
- **Kotlinx Serialization**: JSON serialization
- **Kotlinx Coroutines**: Asynchronous programming