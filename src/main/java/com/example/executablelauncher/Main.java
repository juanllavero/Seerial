package com.example.executablelauncher;

import com.example.executablelauncher.utils.Configuration;

public class Main {
    public static void main(String[] args) {
        Configuration.saveConfig("fullscreen", "off");
        App.main(args);
    }
}
