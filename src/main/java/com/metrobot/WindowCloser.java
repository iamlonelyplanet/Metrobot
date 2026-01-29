package com.metrobot;

import com.sun.jna.platform.win32.*;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;

import java.util.List;


public class WindowCloser {
    /**
     * Закрывает все переданные окна.
     */
    public static void closeWindows(List<HWND> windows) {
        if (windows == null || windows.isEmpty()) {
            return;
        }

        for (HWND hwnd : windows) {
            if (User32.INSTANCE.IsWindow(hwnd)) {
                User32.INSTANCE.PostMessage(hwnd, WinUser.WM_CLOSE, null, null);
            }
        }

        // 2. Небольшая пауза — даём диалогу появиться
        sleep(300);

        // 3. Кликаем мышью в центр активного окна
        clickCenterOfForegroundWindow();
    }

    private static void clickCenterOfForegroundWindow() {

        HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        if (hwnd == null) {
            return;
        }

        WinDef.RECT rect = new WinDef.RECT();
        if (!User32.INSTANCE.GetWindowRect(hwnd, rect)) {
            return;
        }

        int centerX = (rect.left + rect.right) / 2;
        int centerY = (rect.top + rect.bottom) / 2;

//        clickButton(String buttonName);
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }
}
