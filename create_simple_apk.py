#!/usr/bin/env python3
"""
Create a simple APK using alternative methods
"""

import os
import subprocess
import shutil

def create_cordova_app():
    """Create a Cordova-based APK from our PWA"""
    print("🔧 Creating Cordova-based APK...")
    
    # Check if cordova is available
    try:
        subprocess.run(['cordova', '--version'], check=True, capture_output=True)
        print("✅ Cordova is available")
    except (subprocess.CalledProcessError, FileNotFoundError):
        print("❌ Cordova not found. Installing...")
        try:
            subprocess.run(['npm', 'install', '-g', 'cordova'], check=True)
            print("✅ Cordova installed")
        except Exception as e:
            print(f"❌ Failed to install Cordova: {e}")
            return False
    
    # Create Cordova project
    try:
        if os.path.exists('autoscroll-cordova'):
            shutil.rmtree('autoscroll-cordova')
        
        subprocess.run([
            'cordova', 'create', 'autoscroll-cordova', 
            'com.deathknell837.autoscroll', 'AutoScrollReader'
        ], check=True)
        
        # Copy our PWA files
        www_dir = 'autoscroll-cordova/www'
        shutil.copy('web_autoscroll.html', f'{www_dir}/index.html')
        shutil.copy('manifest.json', f'{www_dir}/manifest.json')
        shutil.copy('sw.js', f'{www_dir}/sw.js')
        
        # Add Android platform
        os.chdir('autoscroll-cordova')
        subprocess.run(['cordova', 'platform', 'add', 'android'], check=True)
        
        # Build APK
        subprocess.run(['cordova', 'build', 'android'], check=True)
        
        print("✅ Cordova APK created successfully!")
        return True
        
    except Exception as e:
        print(f"❌ Cordova build failed: {e}")
        return False

def create_webview_apk():
    """Create a simple WebView APK"""
    print("🔧 Creating WebView-based APK...")
    
    # Create Android project structure
    project_dir = "autoscroll-webview"
    if os.path.exists(project_dir):
        shutil.rmtree(project_dir)
    
    os.makedirs(f"{project_dir}/app/src/main/java/com/deathknell837/autoscroll", exist_ok=True)
    os.makedirs(f"{project_dir}/app/src/main/res/layout", exist_ok=True)
    os.makedirs(f"{project_dir}/app/src/main/res/values", exist_ok=True)
    os.makedirs(f"{project_dir}/app/src/main/assets", exist_ok=True)
    
    # Copy web files to assets
    shutil.copy('web_autoscroll.html', f'{project_dir}/app/src/main/assets/index.html')
    shutil.copy('manifest.json', f'{project_dir}/app/src/main/assets/')
    shutil.copy('sw.js', f'{project_dir}/app/src/main/assets/')
    
    # Create MainActivity.java
    main_activity = '''package com.deathknell837.autoscroll;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/index.html");
    }
}'''
    
    with open(f"{project_dir}/app/src/main/java/com/deathknell837/autoscroll/MainActivity.java", 'w') as f:
        f.write(main_activity)
    
    # Create layout
    layout_xml = '''<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <WebView
        android:id="@+id/webview"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

</LinearLayout>'''
    
    with open(f"{project_dir}/app/src/main/res/layout/activity_main.xml", 'w') as f:
        f.write(layout_xml)
    
    # Create strings.xml
    strings_xml = '''<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">AutoScroll Text Reader</string>
</resources>'''
    
    with open(f"{project_dir}/app/src/main/res/values/strings.xml", 'w') as f:
        f.write(strings_xml)
    
    print("✅ WebView project structure created")
    print(f"📁 Project created in: {project_dir}")
    print("ℹ️  You can now import this into Android Studio to build the APK")
    
    return True

def main():
    """Main function to create APK"""
    print("🚀 AutoScroll APK Creator")
    print("=" * 50)
    
    print("Available methods:")
    print("1. Cordova-based APK (requires Node.js)")
    print("2. WebView-based APK (requires Android Studio)")
    
    choice = input("Choose method (1 or 2): ").strip()
    
    if choice == "1":
        success = create_cordova_app()
    elif choice == "2":
        success = create_webview_apk()
    else:
        print("❌ Invalid choice")
        return
    
    if success:
        print("🎉 APK creation process completed!")
        print("📱 You can now install the APK on your phone")
    else:
        print("❌ APK creation failed")

if __name__ == "__main__":
    main()