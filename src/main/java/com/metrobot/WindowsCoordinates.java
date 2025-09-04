package com.metrobot;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.*;

import java.util.*;

public class WindowsCoordinates {
    private static final User32 user32 = User32.INSTANCE;
    private final Map<String, HWND> windowsByRole = new HashMap<>();

    public WindowsCoordinates() {
        detectWindows();
    }

    private void detectWindows() {
        List<HWND> foundWindows = new ArrayList<>();

        user32.EnumWindows((hWnd, data) -> {
            char[] buffer = new char[512];
            user32.GetWindowText(hWnd, buffer, 512);
            String title = Native.toString(buffer);

            if (title != null && title.equals("Игроклуб Mail.ru")) {
                foundWindows.add(hWnd);
            }
            return true;
        }, null);

        // сортировка: верхний левый, верхний правый, нижний левый, нижний правый
        foundWindows.sort(Comparator.comparingInt((HWND h) -> {
            RECT rect = new RECT();
            user32.GetWindowRect(h, rect);
            return rect.top * 10_000 + rect.left;
        }));

        if (foundWindows.size() >= 4) {
            windowsByRole.put("Ф1", foundWindows.get(0));
            windowsByRole.put("Лёха-156", foundWindows.get(1));
            windowsByRole.put("Хуан", foundWindows.get(2));
            windowsByRole.put("Антон", foundWindows.get(3));
        }
    }

    public HWND getWindow(String role) {
        return windowsByRole.get(role);
    }
}