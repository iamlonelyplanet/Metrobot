package com.metrobot;

import net.sourceforge.tess4j.Tesseract;

import java.awt.*;
import java.awt.image.*;

public class ScreenReader {

    private final Tesseract tesseract;

    public ScreenReader() {
        tesseract = new Tesseract();
        tesseract.setDatapath("C:\\Tess4J\\tessdata"); // путь к языковым данным
        tesseract.setLanguage("eng"); // или rus, если нужно
    }

    public String readScreen() {
        try {
            BufferedImage screenshot = captureFullScreen();
            return tesseract.doOCR(screenshot);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public class ImagePreprocessor {

        public static BufferedImage preprocess(BufferedImage input) {
            // 1. Масштабируем (например, в 2 раза для читаемости шрифта)
            int newWidth = input.getWidth() * 2;
            int newHeight = input.getHeight() * 2;
            BufferedImage scaled = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = scaled.createGraphics();
            g2d.drawImage(input, 0, 0, newWidth, newHeight, null);
            g2d.dispose();

            // 2. Переводим в градации серого
            BufferedImage gray = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
            ColorConvertOp op = new ColorConvertOp(scaled.getColorModel().getColorSpace(),
                    gray.getColorModel().getColorSpace(), null);
            op.filter(scaled, gray);

            // 3. Немного повышаем контраст
            RescaleOp rescaleOp = new RescaleOp(1.5f, 0, null); // множитель, смещение
            rescaleOp.filter(gray, gray);

            // 4. Лёгкое размытие/сглаживание перед бинаризацией (по желанию)
            float[] blurKernel = {
                    1f/9f, 1f/9f, 1f/9f,
                    1f/9f, 1f/9f, 1f/9f,
                    1f/9f, 1f/9f, 1f/9f
            };
            ConvolveOp blur = new ConvolveOp(new Kernel(3, 3, blurKernel));
            blur.filter(gray, gray);

            // 5. Простейшая бинаризация (порог можно подобрать)
            BufferedImage binary = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_BINARY);
            for (int y = 0; y < newHeight; y++) {
                for (int x = 0; x < newWidth; x++) {
                    int rgb = gray.getRGB(x, y) & 0xFF;
                    if (rgb < 128) {
                        binary.setRGB(x, y, 0xFF000000); // чёрный
                    } else {
                        binary.setRGB(x, y, 0xFFFFFFFF); // белый
                    }
                }
            }

            return binary;
        }
    }

    private BufferedImage captureFullScreen() throws AWTException {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return new Robot().createScreenCapture(screenRect);
    }
}
