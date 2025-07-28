#!/usr/bin/env python3
"""
Simple AutoScroll App - Android Version without Kivy
Uses basic Python with minimal dependencies for APK building
"""

import time
import threading
import sys
import os

class SimpleAutoScrollApp:
    def __init__(self):
        self.text = ""
        self.scroll_speed = 1.0
        self.is_scrolling = False
        self.scroll_position = 0
        self.running = True
        
    def load_text(self, text_content):
        """Load text for scrolling"""
        self.text = text_content
        self.scroll_position = 0
        print(f"✅ Text loaded: {len(text_content)} characters")
        
    def set_speed(self, speed):
        """Set scrolling speed"""
        self.scroll_speed = max(0.1, min(5.0, speed))
        print(f"⚡ Speed set to: {self.scroll_speed}x")
        
    def start_scroll(self):
        """Start auto-scrolling"""
        if not self.text:
            print("❌ No text loaded!")
            return
            
        self.is_scrolling = True
        print("▶️ Starting auto-scroll...")
        
        def scroll_thread():
            while self.is_scrolling and self.scroll_position < len(self.text):
                # Display current portion of text
                end_pos = min(self.scroll_position + 100, len(self.text))
                current_text = self.text[self.scroll_position:end_pos]
                
                # Clear screen and show text
                os.system('clear' if os.name == 'posix' else 'cls')
                print("=" * 50)
                print("📱 AutoScroll Text Reader")
                print(f"⚡ Speed: {self.scroll_speed}x | Position: {self.scroll_position}/{len(self.text)}")
                print("=" * 50)
                print(current_text)
                print("=" * 50)
                print("Commands: [p]ause, [r]eset, [q]uit")
                
                # Advance position
                self.scroll_position += int(10 * self.scroll_speed)
                
                # Wait based on speed
                time.sleep(1.0 / self.scroll_speed)
                
            if self.scroll_position >= len(self.text):
                print("🏁 Reached end of text!")
                self.is_scrolling = False
                
        threading.Thread(target=scroll_thread, daemon=True).start()
        
    def pause_scroll(self):
        """Pause scrolling"""
        self.is_scrolling = False
        print("⏸️ Scrolling paused")
        
    def reset_scroll(self):
        """Reset to beginning"""
        self.scroll_position = 0
        print("⏮️ Reset to beginning")
        
    def run_interactive(self):
        """Run interactive mode"""
        print("🚀 AutoScroll Text Reader - Android Edition")
        print("=" * 50)
        
        # Sample text
        sample_text = """Welcome to AutoScroll Text Reader!

This is a mobile automatic text scrolling application designed for Android devices. 

Features:
• Automatic text scrolling with adjustable speed
• Play/pause controls
• Reset functionality  
• Mobile-optimized interface
• Works on Android phones and tablets

Instructions:
1. Load your text content
2. Adjust the scrolling speed
3. Start auto-scrolling
4. Enjoy hands-free reading!

This app is perfect for:
- Reading long articles
- Studying documents
- Hands-free content consumption
- Accessibility needs

The app automatically scrolls through your text at a comfortable pace, allowing you to read without touching the screen. You can adjust the speed from very slow (0.1x) to very fast (5.0x) to match your reading preference.

Built with Python for maximum compatibility and performance on Android devices."""

        self.load_text(sample_text)
        
        while self.running:
            print("\n📱 AutoScroll Commands:")
            print("1. [s] Start scrolling")
            print("2. [p] Pause scrolling") 
            print("3. [r] Reset to beginning")
            print("4. [+] Increase speed")
            print("5. [-] Decrease speed")
            print("6. [t] Load custom text")
            print("7. [q] Quit")
            
            try:
                choice = input("\nEnter command: ").lower().strip()
                
                if choice == 's':
                    self.start_scroll()
                elif choice == 'p':
                    self.pause_scroll()
                elif choice == 'r':
                    self.reset_scroll()
                elif choice == '+':
                    self.set_speed(self.scroll_speed + 0.1)
                elif choice == '-':
                    self.set_speed(self.scroll_speed - 0.1)
                elif choice == 't':
                    print("Enter your text (press Enter twice to finish):")
                    lines = []
                    while True:
                        line = input()
                        if line == "" and len(lines) > 0 and lines[-1] == "":
                            break
                        lines.append(line)
                    custom_text = "\n".join(lines[:-1])  # Remove last empty line
                    if custom_text.strip():
                        self.load_text(custom_text)
                elif choice == 'q':
                    self.running = False
                    print("👋 Goodbye!")
                else:
                    print("❌ Invalid command")
                    
            except KeyboardInterrupt:
                self.running = False
                print("\n👋 Goodbye!")
                break
            except Exception as e:
                print(f"❌ Error: {e}")

def main():
    """Main entry point"""
    app = SimpleAutoScrollApp()
    
    # Check if running on Android
    try:
        import android
        print("📱 Running on Android device")
    except ImportError:
        print("💻 Running on desktop/Termux")
    
    app.run_interactive()

if __name__ == "__main__":
    main()