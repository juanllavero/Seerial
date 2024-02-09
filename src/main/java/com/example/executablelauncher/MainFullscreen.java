package com.example.executablelauncher;

import com.example.executablelauncher.utils.Configuration;

public class MainFullscreen {
    public static void main(String[] args) {
        Configuration.saveConfig("fullscreen", "on");
        App.main(args);
    }
}
