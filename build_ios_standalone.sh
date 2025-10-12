#!/bin/bash
# Script to build iOS framework outside of Xcode
# This bypasses the need for Xcode environment variables

set -e  # Exit on error

# -----------------------------
# Configuration
# -----------------------------
ENVIRONMENT="development"

usage() {
    cat <<EOF
Usage: $0 [--env <environment>]

Options:
  --env, -e   Target environment: development | staging | production
              (aliases: dev, stage, prod, release)
EOF
}

normalize_env() {
    local input="$1"
    input=$(printf '%s' "$input" | tr '[:upper:]' '[:lower:]')
    case "$input" in
        production|prod|release)
            echo "production"
            ;;
        staging|stage|preprod)
            echo "staging"
            ;;
        development|dev|debug)
            echo "development"
            ;;
        "")
            echo "development"
            ;;
        *)
            echo "Unknown environment: $1" >&2
            usage
            exit 1
            ;;
    esac
}

while [[ $# -gt 0 ]]; do
    case "$1" in
        --env|-e)
            if [[ -z "$2" ]]; then
                echo "Error: --env requires a value" >&2
                usage
                exit 1
            fi
            ENVIRONMENT=$(normalize_env "$2")
            shift 2
            ;;
        --help|-h)
            usage
            exit 0
            ;;
        *)
            echo "Unknown argument: $1" >&2
            usage
            exit 1
            ;;
    esac
done

echo "===== Building Standalone iOS Framework ====="
echo "Using environment: $ENVIRONMENT"

# 0. Update Info.plist AppEnvironment entry so runtime can detect the environment
PLIST_PATH="iosApp/iosApp/Info.plist"
if [[ -f "$PLIST_PATH" ]]; then
    if /usr/libexec/PlistBuddy -c "Print :AppEnvironment" "$PLIST_PATH" >/dev/null 2>&1; then
        /usr/libexec/PlistBuddy -c "Set :AppEnvironment $ENVIRONMENT" "$PLIST_PATH"
    else
        /usr/libexec/PlistBuddy -c "Add :AppEnvironment string $ENVIRONMENT" "$PLIST_PATH"
    fi
    echo "Set AppEnvironment in Info.plist to '$ENVIRONMENT'"
else
    echo "Warning: Info.plist not found at $PLIST_PATH; cannot persist environment"
fi

# 1. Clean any existing build artifacts
echo "Cleaning old build files..."
APP_ENV="$ENVIRONMENT" ./gradlew clean

# 2. Create necessary directories
echo "Creating framework directory structure..."
mkdir -p composeApp/build/cocoapods/framework/ComposeApp.framework/Resources

# 3. Build the framework for iOS simulator
echo "Building framework for iOS Simulator ARM64..."
APP_ENV="$ENVIRONMENT" ./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64

# 4. Copy the built framework to the expected location
echo "Copying framework to expected location..."
mkdir -p composeApp/build/cocoapods/framework
cp -R composeApp/build/bin/iosSimulatorArm64/debugFramework/ComposeApp.framework composeApp/build/cocoapods/framework/

# 5. Using the correct resource path based on the search result
echo "Checking for drawable resources..."
DRAWABLE_DIR="composeApp/src/commonMain/composeResources/drawable"

# The exact path structure needed by the Compose resources system
echo "Creating all required resource directories..."
mkdir -p composeApp/build/cocoapods/framework/ComposeApp.framework/Resources/compose-resources/composeResources/livestockwealth.composeapp.generated.resources/drawable

if [ -d "$DRAWABLE_DIR" ]; then
  echo "Found drawable resources at: $DRAWABLE_DIR"
  echo "Copying drawable resources to all possible locations..."
  # Copy to the exact path mentioned in the error message
  cp -R "$DRAWABLE_DIR"/* composeApp/build/cocoapods/framework/ComposeApp.framework/Resources/compose-resources/composeResources/livestockwealth.composeapp.generated.resources/drawable/
  # Also copy to the top level Resources just in case
  cp -R "$DRAWABLE_DIR"/* composeApp/build/cocoapods/framework/ComposeApp.framework/Resources/
  echo "Resources copied successfully to multiple locations"
  
  # List the resource directories to confirm
  echo "Resources in framework:"
  find composeApp/build/cocoapods/framework/ComposeApp.framework/Resources -type f | sort
fi

# Let's try to also run the necessary Compose resource tasks
echo "Running Compose resource generation tasks..."
APP_ENV="$ENVIRONMENT" ./gradlew :composeApp:generateComposeResClass

# 6. Update our podspec for better resource handling
echo "Updating podspec..."
cat > composeApp/ComposeApp.podspec << EOL
Pod::Spec.new do |spec|
    spec.name                     = 'ComposeApp'
    spec.version                  = '1.0'
    spec.homepage                 = 'https://github.com/JohnDoe/ComposeApp'
    spec.source                   = { :git => 'Not Published', :tag => '1.0' }
    spec.authors                  = ''
    spec.license                  = ''
    spec.summary                  = 'Livestock Wealth KMP application'
    
    spec.vendored_frameworks      = 'build/cocoapods/framework/ComposeApp.framework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target = '14.1'
    
    # Include resources from the framework directly
    spec.resource = ['build/cocoapods/framework/ComposeApp.framework/Resources/**/*']
    
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':composeApp',
        'PRODUCT_MODULE_NAME' => 'ComposeApp',
    }
    
    # Copy the pre-built framework and its resources
    spec.script_phases = [
        {
            :name => 'Copy pre-built framework',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                set -e
                REPO_ROOT="\$PODS_TARGET_SRCROOT"
                
                # Skip during indexing
                if [ "\$ENABLE_PREVIEWS" = "YES" ]; then
                    echo "Skipping framework copy during indexing"
                    exit 0
                fi
                
                # Create the destination directory
                mkdir -p "\${PODS_CONFIGURATION_BUILD_DIR}/ComposeApp"
                
                # Copy the pre-built framework
                if [ -d "\$REPO_ROOT/build/cocoapods/framework/ComposeApp.framework" ]; then
                    cp -R "\$REPO_ROOT/build/cocoapods/framework/ComposeApp.framework" "\${PODS_CONFIGURATION_BUILD_DIR}/ComposeApp/"
                    echo "Copied pre-built framework successfully"
                    
                    # Make sure compose-resources directory exists in the final bundle location
                    mkdir -p "\${CONFIGURATION_BUILD_DIR}/\${UNLOCALIZED_RESOURCES_FOLDER_PATH}/compose-resources/composeResources/livestockwealth.composeapp.generated.resources/drawable"
                    
                    # Copy resources to the final app bundle path
                    if [ -d "\$REPO_ROOT/build/cocoapods/framework/ComposeApp.framework/Resources/compose-resources" ]; then
                        cp -R "\$REPO_ROOT/build/cocoapods/framework/ComposeApp.framework/Resources/compose-resources" "\${CONFIGURATION_BUILD_DIR}/\${UNLOCALIZED_RESOURCES_FOLDER_PATH}/"
                        echo "Copied resources to app bundle"
                        
                        # List resources in the destination to confirm
                        find "\${CONFIGURATION_BUILD_DIR}/\${UNLOCALIZED_RESOURCES_FOLDER_PATH}/compose-resources" -type f | sort
                    else
                        echo "Warning: compose-resources directory not found"
                    fi
                else
                    echo "ERROR: Pre-built framework not found at expected location"
                    echo "Please run the build_ios_standalone.sh script first"
                    exit 1
                fi
            SCRIPT
        }
    ]
end
EOL

# 7. Reinstall pods
echo "===== Reinstalling CocoaPods ====="
cd iosApp
rm -rf Pods Podfile.lock
APP_ENV="$ENVIRONMENT" pod install

echo "===== Setup complete! ====="
echo ""
echo "NEXT STEPS:"
echo "1. Open iosApp/iosApp.xcworkspace in Xcode"
echo "2. Build and run the app on a simulator"
echo ""
echo "If you still see resource errors, let's try a more direct approach by modifying the code."
