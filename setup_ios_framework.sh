#!/bin/bash
set -e

echo "=== Cleaning Build Directories ==="
rm -rf composeApp/build/cocoapods
mkdir -p composeApp/build/cocoapods/framework

echo "=== Creating Framework Structure ==="
FRAMEWORK_DIR="composeApp/build/cocoapods/framework/ComposeApp.framework"
mkdir -p "$FRAMEWORK_DIR"
mkdir -p "$FRAMEWORK_DIR/Headers"
mkdir -p "$FRAMEWORK_DIR/Modules"
mkdir -p "$FRAMEWORK_DIR/Resources"

echo "=== Building iOS Frameworks ==="
# Build for simulator
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
./gradlew :composeApp:linkDebugFrameworkIosX64

# Build for device
./gradlew :composeApp:linkDebugFrameworkIosArm64

echo "=== Reinstalling Pods ==="
cd iosApp
rm -rf Pods
rm -f Podfile.lock
pod install

echo "=== All done! ==="
echo "Now open iosApp/iosApp.xcworkspace in Xcode"
