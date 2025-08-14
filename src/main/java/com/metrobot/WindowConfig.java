package com.metrobot;

import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Конфигурация окон и кнопок относительно верхнего левого угла окна.
 */
public class WindowConfig {
    public static class GameWindow {
        public final String name;
        public final Point topLeft;

        public GameWindow(String name, int x, int y) {
            this.name = name;
            this.topLeft = new Point(x, y);
        }
    }

    // 4 окна (как ты дал)
    public static final GameWindow F1 = new GameWindow("F1", 0, 0);
    public static final GameWindow LEHA = new GameWindow("Лёха-156", 1033, 0);
    public static final GameWindow JUAN = new GameWindow("Хуан", 0, 670);
    public static final GameWindow ANTON = new GameWindow("Антон", 1033, 670);

    // Возвращаем упорядоченную карту: индекс -> окнa (1..4)
    public static Map<Integer, GameWindow> defaultWindows() {
        Map<Integer, GameWindow> m = new LinkedHashMap<>();
        m.put(1, F1);
        m.put(2, LEHA);
        m.put(3, JUAN);
        m.put(4, ANTON);
        return m;
    }

    // Координаты кнопок **относительно верхнего левого угла окна**
    public static final Point BTN_ARENA = new Point(430, 400);
    public static final Point BTN_ATTACK = new Point(390, 610);
    public static final Point BTN_SKIP_BATTLE = new Point(510, 130);
    public static final Point BTN_CLOSE_WIN = new Point(640, 615);   // победа
    public static final Point BTN_CLOSE_LOSE = new Point(640, 560);  // поражение
    public static final Point BTN_GET_PRIZE = new Point(640, 560);  // поражение
}
