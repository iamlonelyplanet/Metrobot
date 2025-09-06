package com.metrobot;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.*;

import java.util.*;

public class WindowsCoordinates {
    private static final User32 user32 = User32.INSTANCE;
    private final Map<String, RECT> coordinatesByRole = new HashMap<>();

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

            if (title != null && title.equals("Игроклуб Mail.ru")) {
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

        // Если нашли хотя бы 4 окна — раскладываем по ролям
        if (foundWindows.size() >= 4) {
            coordinatesByRole.put("Ф1", foundWindows.get(0));        // левое верхнее
            coordinatesByRole.put("Лёха-156", foundWindows.get(1)); // правое верхнее
            coordinatesByRole.put("Хуан", foundWindows.get(2));     // левое нижнее
            coordinatesByRole.put("Антон", foundWindows.get(3));    // правое нижнее
        }
    }

    // Получаем координаты окна по роли
    public RECT getCoordinates(String role) {
        return coordinatesByRole.get(role);
    }

    // Пример: получить левый верхний угол
    public Point getTopLeft(String role) {
        RECT rect = coordinatesByRole.get(role);
        if (rect != null) {
            return new Point(rect.left, rect.top);
        }
        return null;
    }

    // Вспомогательный класс для точки
    public static class Point {
        public final int x;
        public final int y;
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }
}
