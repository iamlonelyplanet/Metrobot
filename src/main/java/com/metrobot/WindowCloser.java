package com.metrobot;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;

public class WindowCloser {
    private static final WinDef.DWORD GW_OWNER = new WinDef.DWORD(WinUser.GW_OWNER);
    private static final int WM_CLOSE = 0x0010;
    private static final int BM_CLICK = 0x00F5;

    public static void closeGameWindow(HWND gameHwnd) {

        // 1. Пытаемся корректно закрыть окно
        User32.INSTANCE.SendMessage(gameHwnd, WM_CLOSE, null, null);

        // 2. Ждём появления диалога подтверждения
        long start = System.currentTimeMillis();
        long timeout = 2000; // 2 секунды — с запасом

        while (System.currentTimeMillis() - start < timeout) {
            HWND confirmDialog = findConfirmationDialog(gameHwnd);
            if (confirmDialog != null) {
                clickYesButton(confirmDialog);
                return;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        }

        // если диалог не появился — значит окно закрылось без подтверждения
    }

    private static HWND findConfirmationDialog(HWND ownerHwnd) {
        final HWND[] result = new HWND[1];

        User32.INSTANCE.EnumWindows((hwnd, data) -> {

            // Проверяем owner
            HWND owner = User32.INSTANCE.GetWindow(hwnd, GW_OWNER);
            if (owner == null || !owner.equals(ownerHwnd)) {
                return true;
            }

            // Проверяем класс окна
            char[] className = new char[512];
            User32.INSTANCE.GetClassName(hwnd, className, 512);
            String cls = Native.toString(className);

            if ("#32770".equals(cls)) { // стандартный dialog
                result[0] = hwnd;
                return false;
            }

            return true;

        }, Pointer.NULL);

        return result[0];
    }

    private static void clickYesButton(HWND dialogHwnd) {

        final HWND[] yesButton = new HWND[1];

        User32.INSTANCE.EnumChildWindows(dialogHwnd, (hwnd, data) -> {

            char[] className = new char[256];
            User32.INSTANCE.GetClassName(hwnd, className, 256);
            if (!"Button".equals(Native.toString(className))) {
                return true;
            }

            char[] text = new char[256];
            User32.INSTANCE.GetWindowText(hwnd, text, 256);
            if ("Да".equals(Native.toString(text))) {
                yesButton[0] = hwnd;
                return false;
            }

            return true;

        }, Pointer.NULL);

        if (yesButton[0] != null) {
            User32.INSTANCE.SendMessage(yesButton[0], BM_CLICK, null, null);
        }
    }
}