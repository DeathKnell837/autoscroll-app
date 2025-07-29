# 🔨 Echo Scroll - APK Build Guide

This guide provides multiple methods to build the Echo Scroll APK, from simple online builders to full Android Studio compilation.

## 🚀 Quick Build Methods

### Method 1: Online APK Builder (Fastest - 5 minutes)

**Using GitHub Codespaces or Gitpod:**

1. **Fork this repository** on GitHub
2. **Open in Codespaces**:
   - Click "Code" → "Codespaces" → "Create codespace"
   - Wait for environment to load
3. **Build APK**:
   ```bash
   cd echo-scroll
   chmod +x build_apk.sh
   ./build_apk.sh
   ```
4. **Download APK** from the codespace

**Using Online Android Builders:**

1. **AppGyver** (https://www.appgyver.com/)
2. **MIT App Inventor** (https://appinventor.mit.edu/)
3. **Thunkable** (https://thunkable.com/)

### Method 2: Android Studio (Professional)

1. **Download Android Studio**: https://developer.android.com/studio
2. **Clone Repository**:
   ```bash
   git clone https://github.com/DeathKnell837/echo-scroll.git
   ```
3. **Open Project** in Android Studio
4. **Sync Gradle** files
5. **Build APK**: Build → Build Bundle(s)/APK(s) → Build APK(s)

### Method 3: Command Line (Advanced)

**Prerequisites:**
- Java 8+ installed
- Android SDK installed
- ANDROID_HOME environment variable set

**Build Steps:**
```bash
# Clone repository
git clone https://github.com/DeathKnell837/echo-scroll.git
cd echo-scroll

# Make gradlew executable
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# APK will be in: app/build/outputs/apk/debug/app-debug.apk
```

## 🛠️ Termux Build Setup

If you want to build directly in Termux (advanced users):

### Install Dependencies
```bash
# Update packages
pkg update && pkg upgrade

# Install required packages
pkg install openjdk-17 gradle android-tools

# Install Android SDK
pkg install android-sdk

# Set environment variables
export ANDROID_HOME=$PREFIX/share/android-sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
```

### Download Required SDK Components
```bash
# Accept licenses
yes | sdkmanager --licenses

# Install required platforms and build tools
sdkmanager "platforms;android-33"
sdkmanager "build-tools;33.0.0"
sdkmanager "platform-tools"
```

### Build APK
```bash
cd echo-scroll
./build_apk.sh
```

## 🔧 Troubleshooting

### Common Issues

**1. "SDK not found" Error**
```bash
# Set ANDROID_HOME correctly
export ANDROID_HOME=/path/to/android-sdk
echo 'export ANDROID_HOME=/path/to/android-sdk' >> ~/.bashrc
```

**2. "Java version incompatible" Error**
```bash
# Check Java version
java -version

# Install correct Java version
pkg install openjdk-17
```

**3. "Gradle build failed" Error**
```bash
# Clean and rebuild
./gradlew clean
./gradlew assembleDebug --stacktrace
```

**4. "Permission denied" Error**
```bash
# Make gradlew executable
chmod +x gradlew
```

### Build Variants

**Debug APK** (for testing):
```bash
./gradlew assembleDebug
```

**Release APK** (for distribution):
```bash
./gradlew assembleRelease
```

## 📱 APK Installation

### Method 1: Direct Installation
```bash
# If device is connected via ADB
adb install echo-scroll-debug.apk
```

### Method 2: Manual Installation
1. Copy APK to your phone
2. Enable "Install from Unknown Sources"
3. Tap the APK file to install

### Method 3: Share via Cloud
1. Upload APK to Google Drive/Dropbox
2. Download on your phone
3. Install from Downloads folder

## 🎯 Pre-built APK

If you just want to use the app without building:

1. **Check Releases**: Look for pre-built APKs in GitHub Releases
2. **Download**: Get the latest `echo-scroll-release.apk`
3. **Install**: Follow installation guide in README.md

## 🔒 Signing APK (For Distribution)

### Generate Keystore
```bash
keytool -genkey -v -keystore echo-scroll.keystore -alias echo-scroll -keyalg RSA -keysize 2048 -validity 10000
```

### Sign APK
```bash
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore echo-scroll.keystore app-release-unsigned.apk echo-scroll
```

### Align APK
```bash
zipalign -v 4 app-release-unsigned.apk echo-scroll-release.apk
```

## 📊 Build Verification

### Check APK Info
```bash
# APK size and details
ls -lh echo-scroll-debug.apk

# APK contents
unzip -l echo-scroll-debug.apk

# APK info with aapt
aapt dump badging echo-scroll-debug.apk
```

### Test Installation
```bash
# Install on connected device
adb install echo-scroll-debug.apk

# Check if installed
adb shell pm list packages | grep echoscroll
```

## 🚀 Automated Build (CI/CD)

### GitHub Actions
Create `.github/workflows/build.yml`:
```yaml
name: Build APK
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    - uses: actions/setup-java@v2
      with:
        java-version: '11'
        distribution: 'adopt'
    - name: Build APK
      run: ./gradlew assembleDebug
    - name: Upload APK
      uses: actions/upload-artifact@v2
      with:
        name: echo-scroll-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

## 💡 Tips for Success

1. **Use Stable Internet**: Downloads can be large
2. **Free Space**: Ensure 2GB+ free space
3. **Patience**: First build takes longer
4. **Clean Builds**: Use `./gradlew clean` if issues occur
5. **Check Logs**: Use `--stacktrace` for detailed errors

## 📞 Need Help?

- **GitHub Issues**: Report build problems
- **Discussions**: Ask questions in GitHub Discussions
- **Documentation**: Check README.md for more info

---

**Happy Building! 🎉**