package com.metrobot;

import java.awt.*;
import java.awt.event.InputEvent;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;

import static com.metrobot.WindowConfig.PAUSE_LONG_MS;
import static com.metrobot.WindowConfig.PAUSE_SHORT_MS;

public abstract class BaseBot {

    // === Общее состояние для всех ботов ===
    protected Robot robot;
    protected List<Integer> windows = new ArrayList<>();
    protected Map<Integer, WindowConfig.GameWindow> windowsMap = WindowConfig.defaultWindows();
    protected boolean silentMode = true;
    protected abstract Map<String, Point> getButtonMap();

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
    protected void countdown(int seconds) throws InterruptedException {
        for (int s = seconds; s > 0; s--) {
            int m = s / 60;
            int ss = s % 60;
            System.out.printf("\rДо следующего боя: %02d:%02d   ", m, ss);
            Thread.sleep(1000); // Не менять, это эталон секунды в счётчике!
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
            Thread.sleep(PAUSE_SHORT_MS);
        }
    }

    protected List<HWND> findGameWindows() {
        List<HWND> res = new ArrayList<>();
        User32.INSTANCE.EnumWindows((hWnd, data) -> {
            char[] buffer = new char[512];
            User32.INSTANCE.GetWindowText(hWnd, buffer, 512);
            String title = new String(buffer).trim();
            if (title.contains("Игроклуб") || title.contains("2033")) res.add(hWnd);
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

    // Свернуть все игровые окна, если включён silentMode
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

            int relX = rel.x;
            int relY = rel.y;

//            // Масштаб учитываем только для окна Ф1.
//            // ВНИМАНИЕ: хотя по замерам зум ≈ 0.913, реально клики совпадают только при 0.96.
//            // Вероятно, Игромир округляет zoom-шаги (100% → 95% → 80% …). Или дело в масштабировании Windows 10.
//            if ("Ф1".equals(gw.name)) {
//                double scale = 0.96; // калибровка для Ctrl–1
//                relX = (int) Math.round(relX * scale);
//                relY = (int) Math.round(relY * scale);
//            }

            int x = gw.topLeft.x + relX;
            int y = gw.topLeft.y + relY;

            clickAt(x, y);
            System.out.printf("%s нажал \"%s\" (%d,%d)%n", gw.name, buttonName, x, y);

            if (i < windows.size() - 1) Thread.sleep(400);
        }
    }

    // --- Клик ---
    protected void clickAt(int x, int y) {
        if (robot == null) return;
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
}
