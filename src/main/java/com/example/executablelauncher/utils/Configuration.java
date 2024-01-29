package com.example.executablelauncher.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

    private static final String CONFIG_FILE = "config.properties";

    public static void saveConfig(String key, String value) {
        Properties properties = new Properties();

        try (FileInputStream fileInput = new FileInputStream(CONFIG_FILE)) {
            properties.load(fileInput);
        } catch (IOException e) {
            //Config file doesn't exist, so it will be created
        }

        properties.setProperty(key, value);

        try (FileOutputStream fileOutput = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fileOutput, null);
        } catch (IOException e) {
            System.err.println("saveConfig: value could not be saved in config.properties");
        }
    }

    public static String loadConfig(String key, String defaultValue) {
        Properties properties = new Properties();

        try (FileInputStream fileInput = new FileInputStream(CONFIG_FILE)) {
            properties.load(fileInput);
        } catch (IOException e) {
            return defaultValue;
        }

        return properties.getProperty(key, defaultValue);
    }
}
