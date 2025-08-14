package com.metrobot;

import java.awt.*;
import java.awt.event.InputEvent;

/**
 * Небольшая утилита для мыши с Robot.
 */
public class MouseUtils {
    private final Robot robot;

    public MouseUtils() throws AWTException {
        this.robot = new Robot();
        // опционально: тонкая настройка
        this.robot.setAutoDelay(80);
    }

    // кликает в абсолютных координатах экрана
    public void click(int x, int y) throws InterruptedException {
        robot.mouseMove(x, y);
        Thread.sleep(120);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(60);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(200);
    }

    // нажать по относительным координатам окна
    public void clickWindow(WindowConfig.GameWindow w, Point relative) throws InterruptedException {
        int x = w.topLeft.x + relative.x;
        int y = w.topLeft.y + relative.y;
        click(x, y);
    }

    // короткая пауза в миллисекундах
    public void sleepMs(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
