#!/bin/bash

# AutoScroll App - APK Build Script
# This script helps build the Android APK

echo "📱 AutoScroll App - APK Builder"
echo "==============================="
echo ""

# Check if buildozer is installed
if ! command -v buildozer &> /dev/null; then
    echo "❌ Buildozer not found!"
    echo "Installing buildozer..."
    pip install buildozer
    echo "✅ Buildozer installed!"
    echo ""
fi

# Check if we're in the right directory
if [ ! -f "buildozer.spec" ]; then
    echo "❌ buildozer.spec not found!"
    echo "Make sure you're in the autoscroll-app directory"
    exit 1
fi

echo "🔧 Build Options:"
echo "1. Debug APK (for testing)"
echo "2. Release APK (for distribution)"
echo "3. Deploy to connected device"
echo "4. Clean build files"
echo ""

read -p "Choose option (1-4): " choice

case $choice in
    1)
        echo "🔨 Building debug APK..."
        buildozer android debug
        echo "✅ Debug APK built! Check bin/ folder"
        ;;
    2)
        echo "🔨 Building release APK..."
        buildozer android release
        echo "✅ Release APK built! Check bin/ folder"
        ;;
    3)
        echo "📱 Building and deploying to device..."
        buildozer android debug deploy
        echo "✅ App deployed to device!"
        ;;
    4)
        echo "🧹 Cleaning build files..."
        buildozer android clean
        echo "✅ Build files cleaned!"
        ;;
    *)
        echo "❌ Invalid option"
        exit 1
        ;;
esac

echo ""
echo "📋 Build Information:"
echo "App Name: AutoScroll Text Reader"
echo "Package: com.deathknell837.autoscroll"
echo "Version: 1.0"
echo ""

if [ -d "bin" ]; then
    echo "📦 Built files:"
    ls -la bin/
fi

echo ""
echo "🎉 Build complete!"