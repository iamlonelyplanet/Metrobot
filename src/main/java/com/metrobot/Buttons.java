package com.metrobot;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/* Набор координат для кнопок, коллекция пауз между кликами, координаты окон.
TODO: объединить все кнопки (координаты) в единое? Повторов хватает. Кнопок не так много. Но и сейчас смотрится красиво.
 */

public class Buttons {
    // --- Паузы ---
    public static final int PAUSE_LONG_MS = 2800;
    public static final int PAUSE_SHORT_MS = 1200;
    public static final int FIVE_MINUTES_PAUSE_SECONDS = 285;
    public static final int PAUSE_BEFORE_BOSS_MS = 12_800;
    public static final int PAUSE_TUNNEL_MS = 16_000;

    // --- Максимально допустимое количество боёв. Почему byte? Где ещё их использовать, если не на учёбе! ---
    public static final byte MAX_BATTLES_ARENA = 50;
    public static final byte MAX_BATTLES_CLANWAR = 24;
    public static final byte MAX_BATTLES_RAID = 12;
    public static final byte MAX_WAYS_TUNNEL = 5;

    // --- Координаты верхних левых углов рабочего поля внутри окна ---
    // Площадь рабочего поля в окне Игромира: 764×650, та же самая в ВК, та же при разрешениях 1366x768, 1080 и 1440.
    // Продолжаем собирать статистику по разрешениям.
    // Площадь стянутого до минимума окна Игромира: 1033x670
    // Ширина полосы прокрутки (элемента окон Windows): 19
    public static int windowWidth = 1033;
    public static int windowHeight = 670;
    public static int xMoveRight = 125; // Расчёт "нуля" рабочего поля
    public static int yMoveDown = 97; // Константа для "Игромира" при WQHD, для остальных - смотреть...

    public static Point topLeft1 = new Point(xMoveRight, yMoveDown);
    public static Point topLeft2 = new Point(windowWidth + xMoveRight, yMoveDown);
    public static Point topLeft3 = new Point(xMoveRight, windowHeight + yMoveDown);
    public static Point topLeft4 = new Point(windowWidth + xMoveRight, windowHeight + yMoveDown);

    public static final GameWindow WINDOW_1 = new GameWindow("Ф1", topLeft1);
    public static final GameWindow WINDOW_2 = new GameWindow("Лёха-156", topLeft2);
    public static final GameWindow WINDOW_3 = new GameWindow("Хуан", topLeft3);
    public static final GameWindow WINDOW_4 = new GameWindow("Антон", topLeft4);

    // Возвращаем упорядоченную карту: индекс -> окно (1..4)
    public static Map<Integer, GameWindow> defaultWindows() {
        Map<Integer, GameWindow> m = new LinkedHashMap<>();
        m.put(1, WINDOW_1);
        m.put(2, WINDOW_2);
        m.put(3, WINDOW_3);
        m.put(4, WINDOW_4);
        return m;
    }

    // === Координаты кнопок относительно верхнего левого угла рабочего поля (не окна!) ===
    // --- Арена ---
    public static final LinkedHashMap<String, Point> ARENA_BUTTONS = new LinkedHashMap<>() {{
        put("Клан - Выход", new Point(55, 505));
        put("Арена", new Point(320, 303));
        put("Атаковать", new Point(200, 513));
        put("Питомец", new Point(55, 505)); // опционально
        put("Пропустить", new Point(385, 33));
        put("Закрыть — Победа", new Point(515, 520));
        put("Закрыть — Поражение", new Point(515, 463));
        put("Забрать коллекцию", new Point(515, 463));
    }};

    // --- Клановая война ---
    public static final LinkedHashMap<String, Point> KV_BUTTONS = new LinkedHashMap<>() {{
        put("Клан", new Point(315, 58));
        put("Война", new Point(55, 303));
        put("Атаковать", new Point(415, 288));
        put("Пропустить", new Point(385, 33));
        put("Закрыть", new Point(515, 433));
        put("Погон", new Point(565, 523));
    }};

    // --- Рейд ---
    public static final LinkedHashMap<String, Point> RAID_BUTTONS = new LinkedHashMap<>() {{
        put("Клан", new Point(315, 58));
        put("Война", new Point(55, 303));
        put("Рейды", new Point(55, 413));
        put("Обновить", new Point(520, 40));
        put("Атаковать", new Point(425, 323));
        put("Пропустить", new Point(385, 33));
        put("Закрыть", new Point(515, 433));
    }};

    // --- Туннели ---
    public static final LinkedHashMap<String, Point> TUNNEL_BUTTONS = new LinkedHashMap<>() {{
        put("В туннель", new Point(415, 273));
        put("Войти", new Point(275, 303));
        put("Войти с пропуском", new Point(275, 373));
        put("Атаковать", new Point(425, 323));
        put("Пропустить", new Point(385, 33));
        put("Закрыть", new Point(515, 520));
        put("Карта-ПК-ФРУ", new Point(315, 478));
        put("Карта-КОМ", new Point(315, 428));
        put("Карта-УНИ", new Point(315, 478));
        put("Карта-ПВ", new Point(315, 533));
        put("Карта-КОМ-ФРУ", new Point(315, 378));
        put("Карта-ФРУ-ПК", new Point(215, 293));
        put("Карта ПК-КРО", new Point(405, 343));
        put("Карта КРО-ПК", new Point(195, 393));
        put("Карта ПКк-ПКг", new Point(185, 433));
        put("Карта ПКг-КИЕ", new Point(225, 303));
        put("Карта КИЕ-ПКг", new Point(205, 433));
        put("Карта ПКг-ПКк", new Point(185, 383));
        put("Питомец", new Point(55, 505)); // опционально
    }};
}