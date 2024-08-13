package com.example.executablelauncher.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;

public class Configuration {
    private static final String ALGORITHM = "AES";
    private static final byte[] KEY = "1234567890123456".getBytes();
    private static final String CONFIG_FILE = "resources/config/config.properties";
    private static final String ENCRYPTED_KEYS = "resources/config/keys.properties.enc";

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

    public static Properties loadEncryptedProperties() throws Exception {
        FileInputStream inputStream = new FileInputStream(ENCRYPTED_KEYS);
        byte[] encryptedData = inputStream.readAllBytes();
        inputStream.close();

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(KEY, ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decryptedData = cipher.doFinal(encryptedData);

        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(decryptedData));
        return properties;
    }
}
