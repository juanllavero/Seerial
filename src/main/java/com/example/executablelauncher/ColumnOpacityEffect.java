package com.example.executablelauncher;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ColumnOpacityEffect {

    public static void main(String[] args) {
        try {
            // Ruta de la imagen original
            String imagePath = "src/main/resources/img/backgrounds/Tengen Toppa Gurren Laggan_0_sb.png";

            // Cargar la imagen desde el archivo
            BufferedImage originalImage = ImageIO.read(new File(imagePath));

            // Aplicar el efecto de opacidad a cada columna
            setTransparencyEffect(originalImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setTransparencyEffect(BufferedImage originalImage) {
        try {
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            int turn = 0;

            // Create copy
            BufferedImage blendedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            float opacity = 1.0f;
            // Apply gradual opacity to columns
            for (int x = 0; x < width; x++) {
                if (turn == 3) {
                    opacity = 1.0f - ((float) x / (width / 3f));
                    turn = 0;
                }else{
                    turn++;
                }

                // Make sure the opacity value is valid
                opacity = Math.min(1.0f, Math.max(0.0f, opacity));

                for (int y = 0; y < height; y++) {
                    // Obtain original pixel's color
                    Color originalColor = new Color(originalImage.getRGB(x, y), true);

                    // Apply opacity
                    int blendedAlpha = (int) (originalColor.getAlpha() * opacity);

                    // Create new color with opacity
                    Color blendedColor = new Color(originalColor.getRed(), originalColor.getGreen(),
                            originalColor.getBlue(), blendedAlpha);

                    // Apply color to the new image
                    blendedImage.setRGB(x, y, blendedColor.getRGB());
                }
            }

            // Save the image with the progressive transparency effect
            ImageIO.write(blendedImage, "png",
                    new File("src/main/resources/img/backgrounds/Tengen Toppa Gurren Laggan_0_21-9.png"));
            originalImage.flush();
            blendedImage.flush();
        } catch (IOException e) {
            System.err.println("AddSeasonController: error applying transparency effect to background");
        }
    }
}