#!/bin/bash

# AutoScroll App - GitHub Repository Setup Script
# This script helps set up the GitHub repository for the AutoScroll app

echo "🚀 AutoScroll App - GitHub Setup"
echo "================================="
echo ""

echo "📋 Repository Information:"
echo "Repository Name: autoscroll-app"
echo "Description: 📱 AutoScroll Text Reader - Mobile automatic text scrolling app for Android with APK support"
echo "Username: DeathKnell837"
echo ""

echo "🔧 Manual GitHub Repository Creation Steps:"
echo ""
echo "1. Go to https://github.com/new"
echo "2. Repository name: autoscroll-app"
echo "3. Description: 📱 AutoScroll Text Reader - Mobile automatic text scrolling app for Android with APK support"
echo "4. Set to Public"
echo "5. DO NOT initialize with README (we already have one)"
echo "6. Click 'Create repository'"
echo ""

echo "📤 After creating the repository, run these commands:"
echo ""
echo "git branch -M main"
echo "git push -u origin main"
echo ""

echo "🎯 Alternative: Use GitHub CLI (if installed):"
echo ""
echo "gh repo create autoscroll-app --public --description \"📱 AutoScroll Text Reader - Mobile automatic text scrolling app for Android with APK support\""
echo "git push -u origin main"
echo ""

echo "✅ Repository is ready to push!"
echo "All files are committed and ready to go."
echo ""

# Check if GitHub CLI is available
if command -v gh &> /dev/null; then
    echo "🔍 GitHub CLI detected! Would you like to create the repository now? (y/n)"
    read -r response
    if [[ "$response" =~ ^[Yy]$ ]]; then
        echo "Creating repository..."
        gh repo create autoscroll-app --public --description "📱 AutoScroll Text Reader - Mobile automatic text scrolling app for Android with APK support"
        echo "Pushing to GitHub..."
        git branch -M main
        git push -u origin main
        echo "✅ Repository created and pushed successfully!"
        echo "🌐 View at: https://github.com/DeathKnell837/autoscroll-app"
    fi
else
    echo "💡 Tip: Install GitHub CLI with 'pkg install gh' for easier repository management"
fi

echo ""
echo "📱 Next Steps:"
echo "1. Build APK: buildozer android debug"
echo "2. Test on device: buildozer android deploy"
echo "3. Create release: buildozer android release"
echo ""
echo "Happy coding! 🎉"