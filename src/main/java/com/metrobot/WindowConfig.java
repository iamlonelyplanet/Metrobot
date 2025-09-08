package com.metrobot;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

// Конфигурация окон и кнопок относительно верхнего левого угла окна.
public class WindowConfig {
    // --- Паузы ---
    public static final long PAUSE_LONG_MS = 4000;
    public static final long PAUSE_SHORT_MS = 1000;
    public static final long FIVE_MINUTES_PAUSE_SECONDS = 285;
    public static final long PAUSE_BEFORE_BOSS_MS = 12_500;

    // ===== ОКНА =====
    public static class GameWindow {
        public final String name;
        public final Point topLeft;

        public GameWindow(String name, int x, int y) {
            this.name = name;
            this.topLeft = new Point(x, y);
        }
    }

    // Возвращаем упорядоченную карту: индекс -> окно (1..4)
    public static final GameWindow ANTON = new GameWindow("Антон", 1033, 670);
    public static final GameWindow F1 = new GameWindow("Ф1", 0, 0);
    public static final GameWindow LEHA = new GameWindow("Лёха-156", 1033, 0);
    public static final GameWindow JUAN = new GameWindow("Хуан", 0, 670);

    public static Map<Integer, GameWindow> defaultWindows() {
        Map<Integer, GameWindow> m = new LinkedHashMap<>();
        m.put(1, F1);
        m.put(2, LEHA);
        m.put(3, JUAN);
        m.put(4, ANTON);
        return m;
    }

    // --- Арена ---
    public static final LinkedHashMap<String, Point> ARENA_BUTTONS = new LinkedHashMap<>() {{
        put("Арена", new Point(445, 400));
        put("Атаковать", new Point(390, 605));
        put("Пропустить", new Point(510, 130));
        put("Закрыть — Победа", new Point(640, 610));
        put("Закрыть — Поражение", new Point(640, 560));
        put("Забрать коллекцию", new Point(640, 560));
    }};

    // --- Клановая война ---
    public static final LinkedHashMap<String, Point> KV_BUTTONS = new LinkedHashMap<>() {{
        put("КВ — Клан", new Point(315, 155));
        put("КВ — Война", new Point(180, 400));
        put("Атаковать", new Point(540, 385));
        put("Пропустить", new Point(510, 130));
        put("Закрыть", new Point(640, 530));
        put("КВ — Погон", new Point(646, 646));
    }};

    // --- Рейд ---
    public static final LinkedHashMap<String, Point> RAID_BUTTONS = new LinkedHashMap<>() {{
        put("Клан", new Point(310, 160));
        put("Рейды", new Point(180, 510));
        put("Обновить", new Point(252, 123));
        put("Атаковать", new Point(549, 420));
        put("Пропустить", new Point(510, 130));
        put("Закрыть", new Point(640, 530));
    }};
}
