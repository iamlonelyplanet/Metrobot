package com.metrobot;

import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Конфигурация окон и кнопок относительно верхнего левого угла окна.
 */
public class WindowConfig {

    // ===== ОКНА =====
    public static class GameWindow {
        public final String name;
        public final Point topLeft;

        public GameWindow(String name, int x, int y) {
            this.name = name;
            this.topLeft = new Point(x, y);
        }
    }

    // 4 окна (фиксированные координаты)
    public static final GameWindow F1 = new GameWindow("Ф1", 0, 0);
    public static final GameWindow LEHA = new GameWindow("Лёха-156", 1033, 0);
    public static final GameWindow JUAN = new GameWindow("Хуан", 0, 670);
    public static final GameWindow ANTON = new GameWindow("Антон", 1033, 670);

    // Возвращаем упорядоченную карту: индекс -> окно (1..4)
    public static Map<Integer, GameWindow> defaultWindows() {
        Map<Integer, GameWindow> m = new LinkedHashMap<>();
        m.put(1, F1);
        m.put(2, LEHA);
        m.put(3, JUAN);
        m.put(4, ANTON);
        return m;
    }

    // ===== КНОПКИ — Арена =====
    public static final Point BTN_ARENA = new Point(430, 400);
    public static final Point BTN_ATTACK = new Point(390, 610);
    public static final Point BTN_SKIP_BATTLE = new Point(510, 130);
    public static final Point BTN_CLOSE_WIN = new Point(640, 615); // победа
    public static final Point BTN_CLOSE_LOSE = new Point(640, 560); // поражение
    public static final Point BTN_GET_PRIZE = new Point(640, 560);

    // --- Арена ---
    public static final LinkedHashMap<String, Point> ARENA_BUTTONS = new LinkedHashMap<>() {{
        put("Арена", BTN_ARENA);
        put("Атаковать", BTN_ATTACK);
        put("Пропустить бой", BTN_SKIP_BATTLE);
        put("Закрыть — Победа", BTN_CLOSE_WIN);
        put("Закрыть — Поражение", BTN_CLOSE_LOSE);
        put("Забрать коллекцию", BTN_GET_PRIZE);
    }};

    // ===== КНОПКИ — КВ (клановая война) =====
    public static final Point KV_CLAN_BUTTON = new Point(290, 160);
    public static final Point KV_WAR_BUTTON = new Point(180, 400);
    public static final Point KV_ATTACK_BUTTON = new Point(540, 385);
    public static final Point KV_SKIP_BATTLE_BUTTON = new Point(510, 130);
    public static final Point KV_CLOSE_BUTTON = new Point(640, 530);

    // --- Клановая война ---
    public static final LinkedHashMap<String, Point> KV_BUTTONS = new LinkedHashMap<>() {{
        put("КВ — Клан", new Point(290, 160));
        put("КВ — Война", new Point(180, 400));
        put("КВ — Атаковать", new Point(540, 385));
        put("КВ — Пропустить бой", new Point(510, 130));
        put("КВ — Закрыть", new Point(640, 530));
    }};

}
