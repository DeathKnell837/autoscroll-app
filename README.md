# 📱 Echo Scroll - Auto-Scrolling for Slow Readers

**Echo Scroll** is an Android accessibility app designed specifically for slow readers. It provides automatic vertical scrolling in any reading app (like QuickNovel, Kindle, Chrome, etc.) with a floating overlay control panel.

## 🚀 **GET STARTED NOW**

### 📱 **Immediate Options**
1. **🌐 Try Web Demo**: Open [`web-version.html`](web-version.html) for instant preview
2. **📦 Build APK**: Use [GitHub Codespaces](https://github.com/codespaces) for free APK building
3. **📥 Download APK**: Check [GitHub Releases](https://github.com/DeathKnell837/echo-scroll/releases) for pre-built APKs

### 📚 **Quick Guides**
- **[⚡ QUICK_START.md](QUICK_START.md)**: 5-minute setup guide
- **[📱 INSTALLATION_GUIDE.md](INSTALLATION_GUIDE.md)**: Complete setup instructions
- **[🔨 BUILD_GUIDE.md](BUILD_GUIDE.md)**: Multiple APK building methods
- **[🎯 APK_SOLUTIONS.md](APK_SOLUTIONS.md)**: Get your APK multiple ways

## 🌟 Features

### ✨ Core Functionality
- **🎯 Overlay Controls**: Floating, draggable panel that stays on top of all apps
- **🔄 Auto-Scrolling**: Automatic vertical scrolling using Android Accessibility Service
- **⚡ Speed Control**: 5 speed levels optimized for slow reading (1000ms to 3000ms delays)
- **🎮 Simple Controls**: Start, Stop, and Speed adjustment buttons
- **📱 Background Service**: Continues scrolling even when app is in background
- **🔧 Non-Intrusive**: Doesn't interfere with taps, typing, or other gestures

### 🎨 User Interface
- **Minimalistic Design**: Clean, light interface optimized for mobile
- **Drag & Drop**: Move the floating panel anywhere on screen
- **Visual Feedback**: Color-coded speed indicators and status updates
- **Material Design**: Modern Android UI following Material Design guidelines

### 🔒 Permissions & Security
- **SYSTEM_ALERT_WINDOW**: For floating overlay controls
- **BIND_ACCESSIBILITY_SERVICE**: For auto-scrolling in other apps
- **FOREGROUND_SERVICE**: For background operation
- **Privacy-First**: No data collection, no internet permissions

## 📋 Speed Levels

| Level | Delay | Description | Use Case |
|-------|-------|-------------|----------|
| 1 | 3000ms | Very Slow | Learning new languages, complex texts |
| 2 | 2500ms | Slow | Technical documentation, studying |
| 3 | 2000ms | Normal | Regular reading, novels |
| 4 | 1500ms | Fast | Familiar content, reviews |
| 5 | 1000ms | Faster | Quick scanning, known material |

## 🚀 Installation & Setup

### 📦 APK Installation
1. Download the APK file from releases
2. Enable "Install from Unknown Sources" in Android settings
3. Install the APK
4. Open Echo Scroll app

### ⚙️ Permission Setup
1. **Enable Accessibility Service**:
   - Tap "Setup Accessibility Service"
   - Find "Echo Scroll" in Accessibility settings
   - Enable the service
   
2. **Grant Overlay Permission**:
   - Tap "Setup Overlay Permission"
   - Allow "Display over other apps"

3. **Start Echo Scroll**:
   - Tap "Start Echo Scroll"
   - Floating controls will appear

## 📖 How to Use

### 🎯 Basic Usage
1. Open your reading app (QuickNovel, Kindle, Chrome, etc.)
2. Position the text where you want to start reading
3. Use the floating Echo Scroll panel:
   - **Start**: Begin auto-scrolling
   - **Stop**: Pause scrolling
   - **1-5 Buttons**: Adjust speed
   - **Drag Handle**: Move the panel

### 💡 Pro Tips
- **Speed Adjustment**: Start with level 3 (Normal) and adjust based on your reading pace
- **Positioning**: Place the floating panel where it doesn't block important content
- **Pause & Resume**: Use Stop/Start to pause for complex sections
- **App Switching**: Echo Scroll works across all apps - switch between reading apps seamlessly

## 🛠️ Technical Details

### 📱 System Requirements
- **Android 6.0+** (API 23+)
- **Accessibility Service Support**
- **Overlay Permission Support**

### 🏗️ Architecture
- **Kotlin**: Modern Android development
- **AccessibilityService**: Core scrolling functionality
- **Foreground Service**: Background operation
- **Overlay Service**: Floating UI controls
- **Material Design**: UI components

### 🔧 Components
- **MainActivity**: Setup and permission management
- **ScrollAccessibilityService**: Handles gesture-based scrolling
- **OverlayService**: Manages floating control panel
- **ScrollForegroundService**: Background service with notifications
- **PermissionHelper**: Utility for permission checks

## 🎯 Supported Apps

Echo Scroll works with any app that supports scrolling, including:

- **📚 Reading Apps**: QuickNovel, Kindle, Google Books, ReadEra
- **🌐 Browsers**: Chrome, Firefox, Edge, Samsung Internet
- **📰 News Apps**: Google News, Reddit, Medium
- **📄 Document Viewers**: Google Drive, Adobe Reader, WPS Office
- **💬 Social Media**: Twitter, Facebook (reading posts)

## 🔧 Development

### 🏗️ Building from Source
```bash
# Clone the repository
git clone https://github.com/DeathKnell837/echo-scroll.git
cd echo-scroll

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

### 📁 Project Structure
```
echo-scroll/
├── app/
│   ├── src/main/
│   │   ├── java/com/echoscroll/
│   │   │   ├── MainActivity.kt
│   │   │   ├── ScrollAccessibilityService.kt
│   │   │   ├── OverlayService.kt
│   │   │   ├── ScrollForegroundService.kt
│   │   │   └── utils/PermissionHelper.kt
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
└── README.md
```

## 🚀 Future Enhancements

### 🎯 Planned Features
- **🧠 Smart Speed**: Auto-adjust based on reading patterns
- **📊 Reading Analytics**: Track reading time and progress
- **🎨 Themes**: Dark mode and custom themes
- **🗣️ Voice Control**: Start/stop with voice commands
- **📱 Gesture Control**: Tap to pause, double-tap to adjust speed
- **📚 App Profiles**: Different settings for different apps
- **🔄 Horizontal Scrolling**: Support for horizontal content
- **⏰ Reading Timer**: Set reading sessions with breaks

### 🛠️ Technical Improvements
- **🔋 Battery Optimization**: More efficient scrolling algorithms
- **🎯 Smart Detection**: Detect when user is actively reading
- **📱 Tablet Support**: Optimized UI for larger screens
- **🌐 Cloud Sync**: Backup settings across devices

## 🤝 Contributing

We welcome contributions! Here's how you can help:

1. **🐛 Bug Reports**: Report issues on GitHub
2. **💡 Feature Requests**: Suggest new features
3. **🔧 Code Contributions**: Submit pull requests
4. **📖 Documentation**: Improve docs and guides
5. **🌍 Translations**: Help translate the app

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Android Accessibility Framework**: For making auto-scrolling possible
- **Material Design**: For beautiful UI components
- **Slow Reading Community**: For inspiration and feedback
- **Open Source Community**: For tools and libraries

## 📞 Support

- **📧 Email**: support@echoscroll.app
- **🐛 Issues**: [GitHub Issues](https://github.com/DeathKnell837/echo-scroll/issues)
- **💬 Discussions**: [GitHub Discussions](https://github.com/DeathKnell837/echo-scroll/discussions)

---

**Made with ❤️ for slow readers everywhere**

*Echo Scroll - Because everyone deserves to read at their own pace*