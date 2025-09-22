package com.metrobot;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;


public class WindowConfig {
    // --- Паузы ---
    public static final int PAUSE_LONG_MS = 2800;
    public static final int PAUSE_SHORT_MS = 1200;
    public static final int FIVE_MINUTES_PAUSE_SECONDS = 285;
    public static final int PAUSE_BEFORE_BOSS_MS = 12_800;
    public static final int PAUSE_TUNNEL_MS = 16_000;

    // --- Максимально допустимое количество боёв ---
    public static final byte MAX_BATTLES_ARENA = 50;
    public static final byte MAX_BATTLES_CLANWAR = 24;
    public static final byte MAX_BATTLES_RAID = 12;
    public static final byte MAX_WAYS_TUNNEL = 5;

    public static final GameWindow ANTON = new GameWindow("Антон", 1033, 670);
    public static final GameWindow F1 = new GameWindow("Ф1", 0, 0);
    public static final GameWindow LEHA = new GameWindow("Лёха-156", 1033, 0);
    public static final GameWindow JUAN = new GameWindow("Хуан", 0, 670);

    // Возвращаем упорядоченную карту: индекс -> окно (1..4)
    public static Map<Integer, GameWindow> defaultWindows() {
        Map<Integer, GameWindow> m = new LinkedHashMap<>();
        m.put(1, F1);
        m.put(2, LEHA);
        m.put(3, JUAN);
        m.put(4, ANTON);
        return m;
    }

    // Конфигурация окон и кнопок относительно верхнего левого угла окна в разных режимах
    // --- Арена ---
    public static final LinkedHashMap<String, Point> ARENA_BUTTONS = new LinkedHashMap<>() {{
        put("Клан - Выход", new Point(180, 600));
        put("Арена", new Point(445, 400));
        put("Атаковать", new Point(325, 610));
        put("Пропустить", new Point(510, 130));
        put("Закрыть — Победа", new Point(640, 610));
        put("Закрыть — Поражение", new Point(640, 560));
        put("Забрать коллекцию", new Point(640, 560));
    }};

    // --- Клановая война ---
    public static final LinkedHashMap<String, Point> KV_BUTTONS = new LinkedHashMap<>() {{
        put("Клан", new Point(440, 155));
        put("Война", new Point(180, 400));
        put("Атаковать", new Point(540, 385));
        put("Пропустить", new Point(510, 130));
        put("Закрыть", new Point(640, 530));
        put("Погон", new Point(690, 620));
    }};

    // --- Рейд ---
    public static final LinkedHashMap<String, Point> RAID_BUTTONS = new LinkedHashMap<>() {{
        put("Клан", new Point(440, 155));
        put("Рейды", new Point(180, 510));
        put("Война", new Point(180, 400));
        put("Обновить", new Point(235, 135));
        put("Обновить2", new Point(650, 140));
        put("Атаковать", new Point(549, 420));
        put("Пропустить", new Point(510, 130));
        put("Закрыть", new Point(640, 530));
    }};

    // --- Туннель ---
    public static final LinkedHashMap<String, Point> TUNNEL_BUTTONS = new LinkedHashMap<>() {{
        put("В туннель", new Point(540,370));
        put("Карта-ПК-ФРУ", new Point(440,575));
        put("Карта-КОМ", new Point(440,525));
        put("Карта-УНИ", new Point(440,575));
        put("Карта-ПВ", new Point(440,630));
        put("Карта-КОМ-ФРУ", new Point(440,475));
        put("Карта-ФРУ-ПК", new Point(340,390));
        put("Войти с пропуском", new Point(400,470));
        put("Войти", new Point(400,400));
        put("Атаковать", new Point(549, 420));
        put("Пропустить", new Point(510, 130));
        put("Закрыть", new Point(640, 610));
        put("Карта ПК-КРО", new Point(530,440));
        put("Карта КРО-ПК", new Point(320,490));
        put("Карта ПКк-ПКг", new Point(310,530));
        put("Карта ПКг-КИЕ", new Point(350,400));
        put("Карта КИЕ-ПКг", new Point(330,530));
        put("Карта ПКг-ПКк", new Point(310,480));
        put("Уйти", new Point(0,0));
    }};

}
