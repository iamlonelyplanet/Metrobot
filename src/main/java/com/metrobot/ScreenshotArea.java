package com.metrobot;

public enum ScreenshotArea {
    TENT(
            43,
            548,
            582,
            22,
            new PixelColorCriteria(200, 150, 160, 20, 30, 10)
    ),

    SEVEN_FRIENDS(
            190,
            510,
            16,
            26,
            new PixelColorCriteria(190, 130, 150, 30, 30, 20)
    );

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final PixelColorCriteria colorCriteria;

    ScreenshotArea(int x, int y, int width, int height, PixelColorCriteria colorCriteria) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.colorCriteria = colorCriteria;
    }

    public record PixelColorCriteria(
            int minRed,
            int minGreen,
            int maxBlue,
            int minRedGreenDiff,
            int minGreenBlueDiff,
            int minPixels
    ) {
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public PixelColorCriteria colorCriteria() {
        return colorCriteria;
    }
}
