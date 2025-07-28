#!/usr/bin/env python3
"""
Simple AutoScroll App - Minimal version for testing
"""

try:
    from kivy.app import App
    from kivy.uix.boxlayout import BoxLayout
    from kivy.uix.button import Button
    from kivy.uix.label import Label
    from kivy.uix.textinput import TextInput
    from kivy.uix.slider import Slider
    from kivy.uix.scrollview import ScrollView
    from kivy.clock import Clock
    from kivy.core.window import Window
    KIVY_AVAILABLE = True
except ImportError:
    KIVY_AVAILABLE = False
    print("Kivy not available. This is a placeholder for the AutoScroll app.")
    print("Install Kivy with: pip install kivy")

if KIVY_AVAILABLE:
    # Set window size for desktop testing
    Window.size = (360, 640)

    class SimpleAutoScrollApp(App):
        def __init__(self, **kwargs):
            super().__init__(**kwargs)
            self.scroll_speed = 1.0
            self.is_scrolling = False
            self.scroll_event = None
            
        def build(self):
            self.title = "AutoScroll Reader"
            
            layout = BoxLayout(orientation='vertical', padding=10, spacing=10)
            
            # Title
            title = Label(text='AutoScroll Text Reader', size_hint_y=None, height=50, font_size='18sp')
            layout.add_widget(title)
            
            # Text input
            self.text_input = TextInput(
                multiline=True,
                hint_text='Enter your text here...',
                size_hint_y=None,
                height=100
            )
            layout.add_widget(self.text_input)
            
            # Load button
            load_btn = Button(text='Load Text', size_hint_y=None, height=40)
            load_btn.bind(on_press=self.load_text)
            layout.add_widget(load_btn)
            
            # Scroll view
            self.scroll_view = ScrollView()
            self.text_label = Label(
                text='Your text will appear here...',
                text_size=(None, None),
                valign='top'
            )
            self.scroll_view.add_widget(self.text_label)
            layout.add_widget(self.scroll_view)
            
            # Controls
            controls = BoxLayout(size_hint_y=None, height=80, orientation='vertical')
            
            # Speed slider
            speed_layout = BoxLayout(size_hint_y=None, height=40)
            speed_layout.add_widget(Label(text='Speed:', size_hint_x=None, width=60))
            self.speed_slider = Slider(min=0.1, max=3.0, value=1.0, step=0.1)
            self.speed_slider.bind(value=self.on_speed_change)
            speed_layout.add_widget(self.speed_slider)
            controls.add_widget(speed_layout)
            
            # Buttons
            btn_layout = BoxLayout(size_hint_y=None, height=40)
            self.play_btn = Button(text='Play')
            self.play_btn.bind(on_press=self.toggle_scroll)
            btn_layout.add_widget(self.play_btn)
            
            reset_btn = Button(text='Reset')
            reset_btn.bind(on_press=self.reset_scroll)
            btn_layout.add_widget(reset_btn)
            
            controls.add_widget(btn_layout)
            layout.add_widget(controls)
            
            return layout
        
        def load_text(self, instance):
            text = self.text_input.text.strip()
            if text:
                self.text_label.text = text
                self.text_label.text_size = (Window.width - 20, None)
                self.scroll_view.scroll_y = 1.0
        
        def toggle_scroll(self, instance):
            if self.is_scrolling:
                self.stop_scroll()
            else:
                self.start_scroll()
        
        def start_scroll(self):
            self.is_scrolling = True
            self.play_btn.text = 'Pause'
            self.scroll_event = Clock.schedule_interval(self.auto_scroll, 1/30.0)
        
        def stop_scroll(self):
            self.is_scrolling = False
            self.play_btn.text = 'Play'
            if self.scroll_event:
                self.scroll_event.cancel()
        
        def auto_scroll(self, dt):
            if not self.is_scrolling:
                return False
            
            scroll_amount = self.scroll_speed * dt * 0.02
            current_scroll = self.scroll_view.scroll_y
            new_scroll = max(0, current_scroll - scroll_amount)
            
            self.scroll_view.scroll_y = new_scroll
            
            if new_scroll <= 0:
                self.stop_scroll()
                return False
            
            return True
        
        def reset_scroll(self, instance):
            self.scroll_view.scroll_y = 1.0
            if self.is_scrolling:
                self.stop_scroll()
        
        def on_speed_change(self, instance, value):
            self.scroll_speed = value

    def main():
        SimpleAutoScrollApp().run()

else:
    def main():
        print("AutoScroll App - Kivy Required")
        print("=" * 40)
        print("This app requires Kivy to run.")
        print("Install with: pip install kivy")
        print("")
        print("Features when Kivy is installed:")
        print("- Automatic text scrolling")
        print("- Adjustable speed control")
        print("- Play/pause functionality")
        print("- Mobile-optimized interface")
        print("- Android APK support")

if __name__ == '__main__':
    main()