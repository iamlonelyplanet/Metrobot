package com.metrobot;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.*;

import java.util.*;

// Этот класс - задел на будущее. Пока (15 сентября) не особо используется. Задумка - брать координаты каждого окна из
// WinAPI вместо того, чтобы задавать их вручную.

public class WindowsCoordinates {
    private static final User32 user32 = User32.INSTANCE;
    private final Map<String, RECT> windowCoordinates = new HashMap<>();

    public WindowsCoordinates() {
        detectWindows();
    }

    private void detectWindows() {
        List<RECT> foundWindows = new ArrayList<>();

        // Ищем все окна с нужным заголовком
        user32.EnumWindows((hWnd, data) -> {
            char[] buffer = new char[512];
            user32.GetWindowText(hWnd, buffer, 512);
            String title = Native.toString(buffer);

            if (title.contains("2033") || title.contains("Игроклуб")) {
                RECT rect = new RECT();
                user32.GetWindowRect(hWnd, rect);
                foundWindows.add(rect);
            }
            return true;
        }, null);

        // Сортировка: сначала по Y (top), потом по X (left)
        foundWindows.sort(Comparator
                .comparingInt((RECT r) -> r.top)
                .thenComparingInt(r -> r.left));

        if (foundWindows.size() >= 4) {
            windowCoordinates.put("Ф1", foundWindows.get(0));        // левое верхнее
            windowCoordinates.put("Лёха-156", foundWindows.get(1)); // правое верхнее
            windowCoordinates.put("Хуан", foundWindows.get(2));     // левое нижнее
            windowCoordinates.put("Антон", foundWindows.get(3));    // правое нижнее
        }
    }

    // Получаем координаты окна
    public RECT getCoordinates(String window) {
        return windowCoordinates.get(window);
    }

    // Получить левый верхний угол
    public Point getTopLeft(String window) {
        RECT rect = windowCoordinates.get(window);
        if (rect != null) {
            return new Point(rect.left, rect.top);
        }
        return null;
    }

    // Получить ширину окна
    public int getWidth(String window) {
        RECT rect = windowCoordinates.get(window);
        if (rect != null) {
            return rect.right - rect.left;
        }
        return -1; // или бросать исключение
    }
    
    // Вспомогательный класс для точки
    public record Point(int x, int y) {
        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }
}
