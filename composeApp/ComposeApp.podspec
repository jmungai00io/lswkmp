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
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                
                # Skip during indexing
                if [ "$ENABLE_PREVIEWS" = "YES" ]; then
                    echo "Skipping framework copy during indexing"
                    exit 0
                fi
                
                # Create the destination directory
                mkdir -p "${PODS_CONFIGURATION_BUILD_DIR}/ComposeApp"
                
                # Copy the pre-built framework
                if [ -d "$REPO_ROOT/build/cocoapods/framework/ComposeApp.framework" ]; then
                    cp -R "$REPO_ROOT/build/cocoapods/framework/ComposeApp.framework" "${PODS_CONFIGURATION_BUILD_DIR}/ComposeApp/"
                    echo "Copied pre-built framework successfully"
                    
                    # Make sure compose-resources directory exists in the final bundle location
                    mkdir -p "${CONFIGURATION_BUILD_DIR}/${UNLOCALIZED_RESOURCES_FOLDER_PATH}/compose-resources/composeResources/livestockwealth.composeapp.generated.resources/drawable"
                    
                    # Copy resources to the final app bundle path
                    if [ -d "$REPO_ROOT/build/cocoapods/framework/ComposeApp.framework/Resources/compose-resources" ]; then
                        cp -R "$REPO_ROOT/build/cocoapods/framework/ComposeApp.framework/Resources/compose-resources" "${CONFIGURATION_BUILD_DIR}/${UNLOCALIZED_RESOURCES_FOLDER_PATH}/"
                        echo "Copied resources to app bundle"
                        
                        # List resources in the destination to confirm
                        find "${CONFIGURATION_BUILD_DIR}/${UNLOCALIZED_RESOURCES_FOLDER_PATH}/compose-resources" -type f | sort
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
