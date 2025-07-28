#!/usr/bin/env python3
"""
AutoScroll App - Mobile Text Auto-Scrolling Application
Created for Android devices with APK support
Author: DeathKnell837
"""

from kivy.app import App
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.button import Button
from kivy.uix.label import Label
from kivy.uix.textinput import TextInput
from kivy.uix.slider import Slider
from kivy.uix.scrollview import ScrollView
from kivy.uix.gridlayout import GridLayout
from kivy.clock import Clock
from kivy.core.window import Window
from kivy.uix.popup import Popup
from kivy.metrics import dp
import os

# Set window size for desktop testing (will be ignored on mobile)
Window.size = (360, 640)

class AutoScrollApp(App):
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.scroll_speed = 1.0  # pixels per frame
        self.is_scrolling = False
        self.scroll_event = None
        self.scroll_view = None
        self.text_label = None
        self.speed_slider = None
        self.play_button = None
        
    def build(self):
        self.title = "AutoScroll - Text Reader"
        
        # Main layout
        main_layout = BoxLayout(orientation='vertical', padding=dp(10), spacing=dp(10))
        
        # Title
        title_label = Label(
            text='AutoScroll Text Reader',
            size_hint_y=None,
            height=dp(50),
            font_size='20sp',
            bold=True,
            color=(0.2, 0.6, 1, 1)
        )
        main_layout.add_widget(title_label)
        
        # Text input area
        input_layout = BoxLayout(orientation='vertical', size_hint_y=None, height=dp(150), spacing=dp(5))
        
        input_label = Label(
            text='Paste or type your text here:',
            size_hint_y=None,
            height=dp(30),
            font_size='14sp',
            text_size=(None, None)
        )
        input_layout.add_widget(input_label)
        
        self.text_input = TextInput(
            multiline=True,
            hint_text='Enter your text here...',
            font_size='14sp',
            size_hint_y=None,
            height=dp(120)
        )
        input_layout.add_widget(self.text_input)
        
        main_layout.add_widget(input_layout)
        
        # Load text button
        load_button = Button(
            text='Load Text for Scrolling',
            size_hint_y=None,
            height=dp(40),
            background_color=(0.2, 0.8, 0.2, 1),
            font_size='16sp'
        )
        load_button.bind(on_press=self.load_text)
        main_layout.add_widget(load_button)
        
        # Scrollable text display
        self.scroll_view = ScrollView(
            do_scroll_x=False,
            do_scroll_y=True,
            bar_width=dp(10),
            scroll_type=['bars', 'content']
        )
        
        self.text_label = Label(
            text='Your text will appear here...\n\nTip: Paste some text above and click "Load Text for Scrolling" to get started!',
            text_size=(None, None),
            valign='top',
            font_size='16sp',
            markup=True,
            color=(0.9, 0.9, 0.9, 1)
        )
        
        self.scroll_view.add_widget(self.text_label)
        main_layout.add_widget(self.scroll_view)
        
        # Control panel
        control_panel = self.create_control_panel()
        main_layout.add_widget(control_panel)
        
        # Bind text input to auto-resize
        self.text_input.bind(text=self.on_text_change)
        
        return main_layout
    
    def create_control_panel(self):
        """Create the control panel with buttons and speed slider"""
        control_layout = BoxLayout(
            orientation='vertical',
            size_hint_y=None,
            height=dp(120),
            spacing=dp(5)
        )
        
        # Speed control
        speed_layout = BoxLayout(orientation='horizontal', size_hint_y=None, height=dp(40))
        
        speed_label = Label(
            text='Speed:',
            size_hint_x=None,
            width=dp(60),
            font_size='14sp'
        )
        speed_layout.add_widget(speed_label)
        
        self.speed_slider = Slider(
            min=0.1,
            max=5.0,
            value=1.0,
            step=0.1,
            size_hint_x=0.7
        )
        self.speed_slider.bind(value=self.on_speed_change)
        speed_layout.add_widget(self.speed_slider)
        
        self.speed_value_label = Label(
            text='1.0x',
            size_hint_x=None,
            width=dp(50),
            font_size='14sp'
        )
        speed_layout.add_widget(self.speed_value_label)
        
        control_layout.add_widget(speed_layout)
        
        # Button controls
        button_layout = GridLayout(cols=3, size_hint_y=None, height=dp(40), spacing=dp(5))
        
        self.play_button = Button(
            text='▶ Play',
            background_color=(0.2, 0.8, 0.2, 1),
            font_size='14sp'
        )
        self.play_button.bind(on_press=self.toggle_scrolling)
        button_layout.add_widget(self.play_button)
        
        reset_button = Button(
            text='⏮ Reset',
            background_color=(0.8, 0.6, 0.2, 1),
            font_size='14sp'
        )
        reset_button.bind(on_press=self.reset_scroll)
        button_layout.add_widget(reset_button)
        
        info_button = Button(
            text='ℹ Info',
            background_color=(0.2, 0.6, 0.8, 1),
            font_size='14sp'
        )
        info_button.bind(on_press=self.show_info)
        button_layout.add_widget(info_button)
        
        control_layout.add_widget(button_layout)
        
        # Status bar
        self.status_label = Label(
            text='Ready - Load text to begin',
            size_hint_y=None,
            height=dp(30),
            font_size='12sp',
            color=(0.7, 0.7, 0.7, 1)
        )
        control_layout.add_widget(self.status_label)
        
        return control_layout
    
    def on_text_change(self, instance, value):
        """Handle text input changes"""
        if len(value) > 0:
            self.status_label.text = f'Text ready ({len(value)} characters) - Click Load Text'
        else:
            self.status_label.text = 'Ready - Enter text to begin'
    
    def load_text(self, instance):
        """Load text from input into the scrollable area"""
        text = self.text_input.text.strip()
        if not text:
            self.show_popup('Error', 'Please enter some text first!')
            return
        
        # Set text with proper sizing
        self.text_label.text = text
        self.text_label.text_size = (Window.width - dp(40), None)
        self.text_label.texture_update()
        
        # Reset scroll position
        self.scroll_view.scroll_y = 1.0
        
        # Update status
        self.status_label.text = f'Text loaded ({len(text)} chars) - Ready to scroll'
        
        # Show success message
        self.show_popup('Success', 'Text loaded successfully!\nUse the controls below to start auto-scrolling.')
    
    def toggle_scrolling(self, instance):
        """Start or stop auto-scrolling"""
        if not self.text_label.text or self.text_label.text == 'Your text will appear here...\n\nTip: Paste some text above and click "Load Text for Scrolling" to get started!':
            self.show_popup('Error', 'Please load some text first!')
            return
        
        if self.is_scrolling:
            self.stop_scrolling()
        else:
            self.start_scrolling()
    
    def start_scrolling(self):
        """Start the auto-scrolling"""
        self.is_scrolling = True
        self.play_button.text = '⏸ Pause'
        self.play_button.background_color = (0.8, 0.2, 0.2, 1)
        self.status_label.text = f'Scrolling at {self.scroll_speed:.1f}x speed'
        
        # Schedule the scrolling function
        self.scroll_event = Clock.schedule_interval(self.auto_scroll, 1/60.0)  # 60 FPS
    
    def stop_scrolling(self):
        """Stop the auto-scrolling"""
        self.is_scrolling = False
        self.play_button.text = '▶ Play'
        self.play_button.background_color = (0.2, 0.8, 0.2, 1)
        self.status_label.text = 'Scrolling paused'
        
        if self.scroll_event:
            self.scroll_event.cancel()
            self.scroll_event = None
    
    def auto_scroll(self, dt):
        """Perform the automatic scrolling"""
        if not self.is_scrolling:
            return False
        
        # Calculate scroll amount based on speed and frame time
        scroll_amount = self.scroll_speed * dt * 0.01  # Adjust multiplier for desired speed
        
        # Update scroll position
        current_scroll = self.scroll_view.scroll_y
        new_scroll = max(0, current_scroll - scroll_amount)
        
        self.scroll_view.scroll_y = new_scroll
        
        # Check if we've reached the bottom
        if new_scroll <= 0:
            self.stop_scrolling()
            self.status_label.text = 'Reached end of text'
            self.show_popup('Finished', 'Reached the end of the text!\nClick Reset to scroll again.')
            return False
        
        return True
    
    def reset_scroll(self, instance):
        """Reset scroll position to top"""
        self.scroll_view.scroll_y = 1.0
        if self.is_scrolling:
            self.stop_scrolling()
        self.status_label.text = 'Reset to top - Ready to scroll'
    
    def on_speed_change(self, instance, value):
        """Handle speed slider changes"""
        self.scroll_speed = value
        self.speed_value_label.text = f'{value:.1f}x'
        if self.is_scrolling:
            self.status_label.text = f'Scrolling at {value:.1f}x speed'
    
    def show_info(self, instance):
        """Show app information"""
        info_text = """AutoScroll Text Reader v1.0

Features:
• Automatic text scrolling
• Adjustable speed (0.1x to 5.0x)
• Play/Pause control
• Reset to top
• Mobile-optimized interface

Instructions:
1. Paste or type text in the input area
2. Click "Load Text for Scrolling"
3. Adjust speed with the slider
4. Press Play to start auto-scrolling
5. Use Pause to stop, Reset to restart

Perfect for reading long articles, documents, or any text content hands-free!

Created by DeathKnell837"""
        
        self.show_popup('About AutoScroll', info_text)
    
    def show_popup(self, title, message):
        """Show a popup message"""
        content = BoxLayout(orientation='vertical', padding=dp(10), spacing=dp(10))
        
        label = Label(
            text=message,
            text_size=(dp(280), None),
            valign='top',
            font_size='14sp'
        )
        content.add_widget(label)
        
        close_button = Button(
            text='OK',
            size_hint_y=None,
            height=dp(40),
            background_color=(0.2, 0.6, 1, 1)
        )
        content.add_widget(close_button)
        
        popup = Popup(
            title=title,
            content=content,
            size_hint=(0.8, 0.6),
            auto_dismiss=False
        )
        
        close_button.bind(on_press=popup.dismiss)
        popup.open()
    
    def on_pause(self):
        """Handle app pause (Android)"""
        if self.is_scrolling:
            self.stop_scrolling()
        return True
    
    def on_resume(self):
        """Handle app resume (Android)"""
        pass

# Sample text for testing
SAMPLE_TEXT = """Welcome to AutoScroll Text Reader!

This is a sample text to demonstrate the auto-scrolling functionality. You can replace this with any text you want to read.

AutoScroll is perfect for:
• Reading long articles
• Studying documents
• Enjoying stories hands-free
• Accessibility needs
• Multitasking while reading

Features include:
• Adjustable scrolling speed
• Play/pause controls
• Reset functionality
• Mobile-optimized interface
• Smooth scrolling animation

To use the app:
1. Clear this text and paste your own content
2. Click "Load Text for Scrolling"
3. Adjust the speed slider to your preference
4. Press the Play button to start auto-scrolling
5. Enjoy hands-free reading!

The app works great on both phones and tablets. The scrolling speed can be adjusted from very slow (0.1x) to very fast (5.0x) to match your reading speed.

You can pause at any time and resume where you left off, or reset to the beginning to read again.

Happy reading with AutoScroll!"""

if __name__ == '__main__':
    # Pre-load sample text for demonstration
    app = AutoScrollApp()
    app.run()