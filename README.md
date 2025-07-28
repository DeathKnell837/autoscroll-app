# AutoScroll Text Reader 📱

A mobile-optimized automatic text scrolling application for Android devices. Perfect for hands-free reading of articles, documents, and any text content.

![AutoScroll App](https://img.shields.io/badge/Platform-Android-green)
![Python](https://img.shields.io/badge/Python-3.8+-blue)
![Kivy](https://img.shields.io/badge/Kivy-2.1.0+-orange)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 🌟 Features

- **Automatic Text Scrolling**: Smooth, continuous scrolling at adjustable speeds
- **Speed Control**: Adjustable from 0.1x to 5.0x speed with real-time slider
- **Play/Pause Control**: Start and stop scrolling at any time
- **Reset Functionality**: Jump back to the beginning instantly
- **Mobile-Optimized UI**: Touch-friendly interface designed for smartphones
- **Text Input**: Paste or type any text content
- **Status Indicators**: Real-time feedback on app status
- **Responsive Design**: Works on various screen sizes
- **Android APK Support**: Ready to build and install on Android devices

## 📱 Screenshots

*Coming soon - Screenshots will be added after first build*

## 🚀 Quick Start

### For Users (APK Installation)

1. Download the latest APK from the [Releases](../../releases) page
2. Enable "Install from Unknown Sources" in your Android settings
3. Install the APK file
4. Open AutoScroll Text Reader
5. Paste your text and start reading!

### For Developers (Build from Source)

#### Prerequisites

- Python 3.8+
- Android SDK and NDK
- Buildozer
- Git

#### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/DeathKnell837/autoscroll-app.git
   cd autoscroll-app
   ```

2. **Install Python dependencies**
   ```bash
   pip install -r requirements.txt
   ```

3. **Install Buildozer (if not already installed)**
   ```bash
   pip install buildozer
   ```

4. **Build APK**
   ```bash
   buildozer android debug
   ```

5. **Install on device**
   ```bash
   buildozer android deploy
   ```

## 📖 How to Use

1. **Launch the App**: Open AutoScroll Text Reader on your device

2. **Add Text**: 
   - Paste text into the input area, or
   - Type your content directly

3. **Load Text**: Click "Load Text for Scrolling" to prepare your content

4. **Adjust Speed**: Use the speed slider to set your preferred reading pace
   - 0.1x = Very slow
   - 1.0x = Normal speed
   - 5.0x = Very fast

5. **Start Reading**: Press the ▶ Play button to begin auto-scrolling

6. **Control Playback**:
   - ⏸ Pause: Stop scrolling temporarily
   - ⏮ Reset: Jump back to the beginning
   - ℹ Info: View app information

## 🛠️ Technical Details

### Built With

- **[Kivy](https://kivy.org/)**: Cross-platform Python framework
- **[Buildozer](https://github.com/kivy/buildozer)**: Android APK packaging tool
- **Python 3.8+**: Core programming language

### Architecture

```
autoscroll-app/
├── main.py              # Main application code
├── buildozer.spec       # APK build configuration
├── requirements.txt     # Python dependencies
├── README.md           # This file
├── .gitignore          # Git ignore rules
└── bin/                # Built APK files (generated)
```

### Key Components

- **AutoScrollApp**: Main application class
- **ScrollView**: Handles text display and scrolling
- **Control Panel**: Speed slider and playback controls
- **Text Input**: Multi-line text entry area
- **Status System**: Real-time feedback and notifications

## 🔧 Development

### Local Testing

Run the app locally for testing:

```bash
python main.py
```

### Building APK

For debug build:
```bash
buildozer android debug
```

For release build:
```bash
buildozer android release
```

### Customization

#### Modify Scroll Speed Range
Edit `main.py` line with `Slider` configuration:
```python
self.speed_slider = Slider(
    min=0.1,    # Minimum speed
    max=5.0,    # Maximum speed
    value=1.0,  # Default speed
    step=0.1    # Speed increment
)
```

#### Change App Appearance
Modify colors and styling in the `build()` method:
```python
# Example: Change title color
title_label = Label(
    text='AutoScroll Text Reader',
    color=(0.2, 0.6, 1, 1)  # RGBA color values
)
```

## 📋 Requirements

### System Requirements

- **Android**: 5.0+ (API level 21+)
- **RAM**: 2GB minimum, 4GB recommended
- **Storage**: 50MB for app installation
- **Permissions**: None required

### Development Requirements

- **Python**: 3.8 or higher
- **Android SDK**: API level 33
- **Android NDK**: Version 25b
- **Buildozer**: 1.5.0+
- **Kivy**: 2.1.0+

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Commit your changes**: `git commit -m 'Add amazing feature'`
4. **Push to the branch**: `git push origin feature/amazing-feature`
5. **Open a Pull Request**

### Development Guidelines

- Follow PEP 8 style guidelines
- Add comments for complex logic
- Test on multiple screen sizes
- Ensure mobile-friendly UI/UX
- Update documentation for new features

## 🐛 Bug Reports

Found a bug? Please open an issue with:

- Device model and Android version
- App version
- Steps to reproduce
- Expected vs actual behavior
- Screenshots (if applicable)

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Kivy Team**: For the excellent cross-platform framework
- **Python Community**: For the amazing ecosystem
- **Android Developers**: For comprehensive documentation
- **Open Source Contributors**: For inspiration and code examples

## 📞 Contact

- **Developer**: DeathKnell837
- **GitHub**: [@DeathKnell837](https://github.com/DeathKnell837)
- **Issues**: [GitHub Issues](../../issues)

## 🔄 Version History

### v1.0.0 (Current)
- Initial release
- Basic auto-scrolling functionality
- Speed control slider
- Play/pause/reset controls
- Mobile-optimized interface
- Android APK support

### Planned Features
- [ ] Dark/Light theme toggle
- [ ] Font size adjustment
- [ ] Bookmark positions
- [ ] Text file import
- [ ] Reading statistics
- [ ] Voice control
- [ ] Tablet optimization

---

**Made with ❤️ for the reading community**

*AutoScroll Text Reader - Making hands-free reading accessible to everyone*