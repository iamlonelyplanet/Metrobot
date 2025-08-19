package com.metrobot;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.List;
import java.util.Map;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;

public abstract class BaseBot {

    protected Robot robot;

    // Пауза между окнами (можно переопределять в наследниках)
    protected long betweenWindowsMs = 500L;

    public BaseBot() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    // ===== Универсальный метод для клика по кнопке =====
    protected void clickAllWindows(
            List<Integer> windows,
            Map<Integer, WindowConfig.GameWindow> winMap,
            Map<String, Point> buttonMap,
            String buttonName
    ) throws InterruptedException {
        Point rel = buttonMap.get(buttonName);
        if (rel == null) {
            System.err.println("⚠ Кнопка \"" + buttonName + "\" не найдена в конфиге");
            return;
        }

        for (int i = 0; i < windows.size(); i++) {
            int idx = windows.get(i);
            WindowConfig.GameWindow gw = winMap.get(idx);
            if (gw == null) continue;

            int x = gw.topLeft.x + rel.x;
            int y = gw.topLeft.y + rel.y;

            clickAt(x, y);
            System.out.printf("Окно \"%s\": нажал \"%s\" (%d,%d)%n",
                    gw.name, buttonName, x, y);

            if (i < windows.size() - 1) Thread.sleep(betweenWindowsMs);
        }
    }

    // ===== Утилиты =====
    protected void clickAt(int x, int y) {
        if (robot == null) return;
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    protected void countdown(long seconds) throws InterruptedException {
        for (long s = seconds; s > 0; s--) {
            long m = s / 60;
            long ss = s % 60;
            System.out.printf("\rДо следующего боя: %02d:%02d   ", m, ss);
            Thread.sleep(1000);
        }
        System.out.println();
    }

/** ===== Работа с окнами Windows =====
    private static final String GAME_WINDOW_TITLE = "Игроклуб Mail.ru";

    protected List<HWND> findGameWindows() {
        java.util.List<HWND> windows = new java.util.ArrayList<>();
        User32.INSTANCE.EnumWindows((hWnd, data) -> {
            char[] buffer = new char[512];
            User32.INSTANCE.GetWindowText(hWnd, buffer, 512);
            String title = new String(buffer).trim();
            if (title.contains(GAME_WINDOW_TITLE)) {
                windows.add(hWnd);
            }
            return true;
        }, null);
        return windows;
    }

    protected void restoreGameWindows() {
        List<HWND> windows = findGameWindows();
        for (HWND hWnd : windows) {
            User32.INSTANCE.ShowWindow(hWnd, User32.SW_RESTORE);
            User32.INSTANCE.SetForegroundWindow(hWnd);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
 */
}
