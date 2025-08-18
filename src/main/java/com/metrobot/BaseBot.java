package com.metrobot;

import java.awt.*;
import java.awt.event.InputEvent;

public abstract class BaseBot {
    protected void countdown(long seconds) throws InterruptedException {
        for (long s = seconds; s > 0; s--) {
            long m = s / 60;
            long ss = s % 60;
            System.out.printf("\rДо следующего боя: %02d:%02d   ", m, ss);
            Thread.sleep(1000);
        }
        System.out.println();
    }

    protected Robot robot;

    public BaseBot() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    protected void clickAt(int x, int y) {
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
}
