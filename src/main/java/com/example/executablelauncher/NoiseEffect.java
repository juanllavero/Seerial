package com.example.executablelauncher;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;

    public class NoiseEffect {

        public static void main(String[] args) {
            try {
                // Cargar la imagen original y la imagen de ruido
                BufferedImage originalImage = ImageIO.read(new File("src/main/resources/img/Background.png"));
                BufferedImage noiseImage2 = ImageIO.read(new File("src/main/resources/img/TEST_blur.png"));
                BufferedImage noiseImage3 = ImageIO.read(new File("src/main/resources/img/TEST_blur.png"));
                BufferedImage noiseImage4 = ImageIO.read(new File("src/main/resources/img/TEST_blur.png"));
                BufferedImage noiseImage5 = ImageIO.read(new File("src/main/resources/img/TEST_blur.png"));
                BufferedImage noiseImage = ImageIO.read(new File("src/main/resources/img/background0.png"));
                //BufferedImage shadow2 = ImageIO.read(new File("src/main/resources/img/desktopBackgroundShadow.png"));
                //BufferedImage shadow3 = ImageIO.read(new File("src/main/resources/img/desktopBackgroundShadow.png"));
                //BufferedImage shadow4 = ImageIO.read(new File("src/main/resources/img/desktopBackgroundShadow.png"));

                // Aumentar el contraste
                float contrastFactor = 0.5f; // Ajusta el valor según tus necesidades
                BufferedImage highContrastImage = applyContrast(originalImage, contrastFactor);

                // Aplicar el efecto de ruido
                BufferedImage resultImage = applyNoiseEffect(highContrastImage, noiseImage2);
                //resultImage = applyNoiseEffect(resultImage, highContrastImage2);
                resultImage = applyNoiseEffect(resultImage, noiseImage);
                resultImage = applyNoiseEffect(resultImage, noiseImage3);
                resultImage = applyNoiseEffect(resultImage, noiseImage4);
                resultImage = applyNoiseEffect(resultImage, noiseImage5);
                //resultImage = applyNoiseEffect(resultImage, shadow3);


                // Guardar la imagen resultante
                ImageIO.write(resultImage, "png", new File("src/main/resources/img/NoiseTEST.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static BufferedImage scaleImageTo(BufferedImage originalImage, int targetWidth, int targetHeight) {
            Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);

            BufferedImage scaledBufferedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = scaledBufferedImage.createGraphics();
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();

            return scaledBufferedImage;
        }

        private static BufferedImage applyNoiseEffect(BufferedImage originalImage, BufferedImage noiseImage) {
            BufferedImage scaledNoiseImage = scaleImageTo(noiseImage, originalImage.getWidth(), originalImage.getHeight());

            int width = Math.min(originalImage.getWidth(), scaledNoiseImage.getWidth());
            int height = Math.min(originalImage.getHeight(), scaledNoiseImage.getHeight());

            BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            float blendFactor = 0.2f;

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color originalColor = new Color(originalImage.getRGB(x, y), true);

                    Color noiseColor = new Color(scaledNoiseImage.getRGB(x, y));

                    int blendedRed = (int) (originalColor.getRed() * (1 - blendFactor) + noiseColor.getRed() * blendFactor);
                    int blendedGreen = (int) (originalColor.getGreen() * (1 - blendFactor) + noiseColor.getGreen() * blendFactor);
                    int blendedBlue = (int) (originalColor.getBlue() * (1 - blendFactor) + noiseColor.getBlue() * blendFactor);
                    int blendedAlpha = originalColor.getAlpha();

                    Color blendedColor = new Color(blendedRed, blendedGreen, blendedBlue, blendedAlpha);
                    resultImage.setRGB(x, y, blendedColor.getRGB());
                }
            }

            return resultImage;
        }

        private static BufferedImage applyContrast(BufferedImage image, float contrastFactor) {
            RescaleOp rescaleOp = new RescaleOp(contrastFactor, 0, null);
            return rescaleOp.filter(image, null);
        }
    }