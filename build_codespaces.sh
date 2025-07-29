#!/bin/bash

# Echo Scroll APK Build Script for GitHub Codespaces
echo "🚀 Building Echo Scroll APK in GitHub Codespaces..."

# Check if we're in Codespaces
if [ -z "$CODESPACES" ]; then
    echo "⚠️  This script is designed for GitHub Codespaces"
    echo "💡 If you're in a different environment, use build_apk.sh instead"
fi

# Install required packages
echo "📦 Installing required packages..."
sudo apt-get update -qq
sudo apt-get install -y openjdk-17-jdk wget unzip

# Set JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$PATH:$JAVA_HOME/bin

echo "☕ Java version:"
java -version

# Download Android SDK Command Line Tools
echo "📱 Setting up Android SDK..."
ANDROID_HOME="$HOME/android-sdk"
mkdir -p $ANDROID_HOME

if [ ! -d "$ANDROID_HOME/cmdline-tools" ]; then
    echo "⬇️  Downloading Android SDK Command Line Tools..."
    cd $HOME
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
    unzip -q commandlinetools-linux-9477386_latest.zip
    mkdir -p $ANDROID_HOME/cmdline-tools
    mv cmdline-tools $ANDROID_HOME/cmdline-tools/latest
    rm commandlinetools-linux-9477386_latest.zip
fi

# Set Android environment variables
export ANDROID_HOME="$HOME/android-sdk"
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

echo "🔧 Android SDK path: $ANDROID_HOME"

# Accept licenses and install required components
echo "📋 Accepting Android SDK licenses..."
yes | sdkmanager --licenses > /dev/null 2>&1

echo "📦 Installing Android SDK components..."
sdkmanager "platforms;android-33" > /dev/null 2>&1
sdkmanager "build-tools;33.0.0" > /dev/null 2>&1
sdkmanager "platform-tools" > /dev/null 2>&1

echo "✅ Android SDK setup complete"

# Navigate to project directory
cd /workspaces/autoscroll-app

# Make gradlew executable
chmod +x gradlew

echo "🧹 Cleaning previous builds..."
./gradlew clean

echo "🔨 Building debug APK..."
./gradlew assembleDebug

# Check if APK was created
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK_PATH" ]; then
    echo "✅ APK built successfully!"
    echo "📱 APK location: $APK_PATH"
    
    # Copy APK to workspace root for easy access
    cp "$APK_PATH" "echo-scroll-debug.apk"
    echo "📋 APK copied to: echo-scroll-debug.apk"
    
    # Show APK info
    echo "📊 APK Information:"
    ls -lh echo-scroll-debug.apk
    
    echo ""
    echo "🎉 Build completed successfully!"
    echo "📱 Your APK is ready: echo-scroll-debug.apk"
    echo "💾 Right-click the file in VS Code and select 'Download' to get it"
    echo ""
    echo "📋 Next steps:"
    echo "1. Download the APK to your computer"
    echo "2. Transfer to your Android phone"
    echo "3. Enable 'Install from Unknown Sources'"
    echo "4. Install the APK"
    echo "5. Setup permissions (Accessibility + Overlay)"
    echo "6. Start using Echo Scroll!"
    
else
    echo "❌ APK build failed!"
    echo "📋 Check the build logs above for errors"
    
    # Show some debug info
    echo ""
    echo "🔍 Debug information:"
    echo "Working directory: $(pwd)"
    echo "Java version: $(java -version 2>&1 | head -1)"
    echo "Android SDK: $ANDROID_HOME"
    echo "Gradle wrapper exists: $(test -f gradlew && echo 'Yes' || echo 'No')"
    
    # Try to show gradle build output
    if [ -f "app/build/outputs/logs/gradle-build.log" ]; then
        echo ""
        echo "📋 Last few lines of build log:"
        tail -20 app/build/outputs/logs/gradle-build.log
    fi
    
    exit 1
fi

echo "🎯 Echo Scroll APK build complete!"