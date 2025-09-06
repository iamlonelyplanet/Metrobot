package com.metrobot;

import java.awt.*;
import java.awt.event.InputEvent;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;

public abstract class BaseBot {

    // === Общее состояние для всех ботов ===
    protected Robot robot;

    // Список выбранных окон (1..4), и карта смещений 1..4 -> GameWindow
    protected List<Integer> windows = new ArrayList<>();
    protected Map<Integer, WindowConfig.GameWindow> windowsMap = WindowConfig.defaultWindows();

    // --- Окна игры (JNA утилиты, как были) ---
    private static final String GAME_WINDOW_TITLE = "Игроклуб Mail.ru";
    protected boolean silentMode = true;

    // --- Конструкторы ---
    public BaseBot() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public BaseBot(List<Integer> windows) {
        this();
        if (windows != null) this.windows = new ArrayList<>(windows);
    }

    // --- Таймер (секунды) ---
    protected void countdown(long seconds) throws InterruptedException {
        for (long s = seconds; s > 0; s--) {
            long m = s / 60;
            long ss = s % 60;
            System.out.printf("\rДо следующего боя: %02d:%02d   ", m, ss);
            Thread.sleep(1000);
        }
        System.out.println();
    }

    // --- Ожидание времени запуска ---
    protected void waitUntilStartTime(LocalTime startTime) throws InterruptedException {
        System.out.printf("Ожидание времени запуска: %02d:%02d...\n",
                startTime.getHour(), startTime.getMinute());
        while (true) {
            LocalTime now = LocalTime.now();
            if (now.getHour() > startTime.getHour()) break;
            if (now.getHour() == startTime.getHour() && now.getMinute() >= startTime.getMinute()) break;
            Thread.sleep(1_000L);
        }
    }

    // --- Клик ---
    protected void clickAt(int x, int y) {
        if (robot == null) return;
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    protected List<HWND> findGameWindows() {
        List<HWND> res = new ArrayList<>();
        User32.INSTANCE.EnumWindows((hWnd, data) -> {
            char[] buffer = new char[512];
            User32.INSTANCE.GetWindowText(hWnd, buffer, 512);
            String title = new String(buffer).trim();
            if (title.contains(GAME_WINDOW_TITLE)) res.add(hWnd);
            return true;
        }, null);
        return res;
    }

    // Развернуть все игровые окна
    protected void showAllGameWindows() {
        List<HWND> wins = findGameWindows();
        for (HWND hWnd : wins) {
            User32.INSTANCE.ShowWindow(hWnd, User32.SW_RESTORE);
            User32.INSTANCE.SetForegroundWindow(hWnd);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Развернул окна");
    }

    // Свернуть обратно, только если включён silentMode
    protected void minimizeAllGameWindows() {
        if (!silentMode) return;
        List<HWND> wins = findGameWindows();
        for (HWND hWnd : wins) {
            User32.INSTANCE.ShowWindow(hWnd, User32.SW_MINIMIZE);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Свернул окна");
    }

    // === Унификация карт кнопок ===
    protected abstract Map<String, Point> getButtonMap();

    // === Единый метод кликов по всем выбранным окнам ===
    protected void clickAllWindows(String buttonName) throws InterruptedException {
        Map<String, Point> buttonMap = getButtonMap();
        Point rel = buttonMap.get(buttonName);
        if (rel == null) {
            System.err.println("⚠ Кнопка \"" + buttonName + "\" не найдена в WindowConfig");
            return;
        }

        for (int i = 0; i < windows.size(); i++) {
            Integer idx = windows.get(i);
            WindowConfig.GameWindow gw = windowsMap.get(idx);
            if (gw == null) continue;

            int x = gw.topLeft.x + rel.x;
            int y = gw.topLeft.y + rel.y;
            clickAt(x, y);
            System.out.printf("%s нажал \"%s\" (%d,%d)%n", gw.name, buttonName, x, y);

            if (i < windows.size() - 1) Thread.sleep(500);
        }
    }
}
