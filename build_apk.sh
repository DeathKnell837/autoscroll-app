#!/bin/bash

# Echo Scroll APK Build Script
echo "🚀 Building Echo Scroll APK..."

# Set Android SDK path
export ANDROID_HOME=/data/data/com.termux/files/home/android-sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin

# Check if Android SDK is available
if [ ! -d "$ANDROID_HOME" ]; then
    echo "❌ Android SDK not found at $ANDROID_HOME"
    exit 1
fi

echo "✅ Android SDK found at $ANDROID_HOME"

# Make gradlew executable
chmod +x gradlew

# Clean previous builds
echo "🧹 Cleaning previous builds..."
./gradlew clean

# Build debug APK
echo "🔨 Building debug APK..."
./gradlew assembleDebug

# Check if APK was created
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK_PATH" ]; then
    echo "✅ APK built successfully!"
    echo "📱 APK location: $APK_PATH"
    
    # Copy APK to root directory for easy access
    cp "$APK_PATH" "echo-scroll-debug.apk"
    echo "📋 APK copied to: echo-scroll-debug.apk"
    
    # Show APK info
    echo "📊 APK Information:"
    ls -lh echo-scroll-debug.apk
else
    echo "❌ APK build failed!"
    echo "📋 Check the build logs above for errors"
    exit 1
fi

echo "🎉 Build completed successfully!"
echo "📱 Install with: adb install echo-scroll-debug.apk"