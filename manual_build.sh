#!/bin/bash

# Echo Scroll Manual APK Build Script
echo "🔨 Building Echo Scroll APK manually..."

# Set paths
ANDROID_SDK="/data/data/com.termux/files/home/android-sdk"
BUILD_TOOLS="$ANDROID_SDK/build-tools/33.0.1"
PLATFORM_TOOLS="$ANDROID_SDK/platform-tools"

# Add tools to PATH
export PATH="$BUILD_TOOLS:$PLATFORM_TOOLS:$PATH"

# Create build directories
mkdir -p build/gen
mkdir -p build/obj
mkdir -p build/apk

echo "✅ Build directories created"

# Generate R.java using aapt
echo "📝 Generating R.java..."
aapt package -f -m \
    -J build/gen \
    -M app/src/main/AndroidManifest.xml \
    -S app/src/main/res \
    -I $ANDROID_SDK/platforms/android-33/android.jar

if [ $? -ne 0 ]; then
    echo "❌ Failed to generate R.java"
    echo "💡 This might be because we don't have the Android platform installed"
    echo "📋 You can build this project using:"
    echo "   1. Android Studio"
    echo "   2. GitHub Codespaces"
    echo "   3. Online APK builders"
    exit 1
fi

echo "✅ R.java generated successfully"

# Compile Java sources
echo "☕ Compiling Java sources..."
javac -d build/obj \
    -classpath $ANDROID_SDK/platforms/android-33/android.jar \
    build/gen/com/echoscroll/R.java \
    app/src/main/java/com/echoscroll/*.java \
    app/src/main/java/com/echoscroll/utils/*.java

if [ $? -ne 0 ]; then
    echo "❌ Java compilation failed"
    exit 1
fi

echo "✅ Java compilation successful"

# Convert to DEX
echo "🔄 Converting to DEX..."
d8 --lib $ANDROID_SDK/platforms/android-33/android.jar \
    --output build/apk/classes.dex \
    build/obj/com/echoscroll/*.class \
    build/obj/com/echoscroll/utils/*.class

if [ $? -ne 0 ]; then
    echo "❌ DEX conversion failed"
    exit 1
fi

echo "✅ DEX conversion successful"

# Package APK
echo "📦 Packaging APK..."
aapt package -f \
    -M app/src/main/AndroidManifest.xml \
    -S app/src/main/res \
    -I $ANDROID_SDK/platforms/android-33/android.jar \
    -F build/echo-scroll-unsigned.apk \
    build/apk

if [ $? -ne 0 ]; then
    echo "❌ APK packaging failed"
    exit 1
fi

echo "✅ APK packaging successful"

# Add DEX to APK
echo "➕ Adding DEX to APK..."
cd build/apk
zip -r ../echo-scroll-unsigned.apk classes.dex
cd ../..

# Align APK
echo "📐 Aligning APK..."
zipalign -f 4 build/echo-scroll-unsigned.apk echo-scroll-debug.apk

if [ $? -ne 0 ]; then
    echo "❌ APK alignment failed"
    exit 1
fi

echo "✅ APK alignment successful"

# Check if APK was created
if [ -f "echo-scroll-debug.apk" ]; then
    echo "🎉 APK built successfully!"
    echo "📱 APK location: echo-scroll-debug.apk"
    echo "📊 APK size: $(ls -lh echo-scroll-debug.apk | awk '{print $5}')"
    echo "📋 Install with: adb install echo-scroll-debug.apk"
else
    echo "❌ APK build failed!"
    exit 1
fi