package com.example.executablelauncher.entities;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class EncryptPropertiesFile {
    private static final String ALGORITHM = "AES";
    private static final byte[] KEY = "1234567890123456".getBytes();

    public static void main(String[] args) throws Exception {
        byte[] fileData = Files.readAllBytes(Paths.get("out/artifacts/Seerial/resources/config/keys.properties"));

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(KEY, ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedData = cipher.doFinal(fileData);

        try (FileOutputStream outputStream = new FileOutputStream("out/artifacts/Seerial/resources/config/keys.properties.enc")) {
            outputStream.write(encryptedData);
        }
    }
}