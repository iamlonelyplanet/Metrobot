package com.metrobot;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

import com.metrobot.WindowsCoordinates;


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
    public static final byte MAX_WAYS_TUNNEL = 1;


    // --- Координаты верхних левых углов ---
    public static final Point TOP_LEFT_1 = new Point(125, 97);
    public static final Point TOP_LEFT_2 = new Point(125 + 1033, 97 + 0);
    public static final Point TOP_LEFT_3 = new Point(125 + 0, 97 + 670);
    public static final Point TOP_LEFT_4 = new Point(125 + 1033, 97 + 670);

    public static final GameWindow WINDOW_1 = new GameWindow("Ф1", TOP_LEFT_1);
    public static final GameWindow WINDOW_2 = new GameWindow("Лёха-156", TOP_LEFT_2);
    public static final GameWindow WINDOW_3 = new GameWindow("Хуан", TOP_LEFT_3);
    public static final GameWindow WINDOW_4 = new GameWindow("Антон", TOP_LEFT_4);

    // Возвращаем упорядоченную карту: индекс -> окно (1..4)
    public static Map<Integer, GameWindow> defaultWindows() {
        Map<Integer, GameWindow> m = new LinkedHashMap<>();
        m.put(1, WINDOW_1);
        m.put(2, WINDOW_2);
        m.put(3, WINDOW_3);
        m.put(4, WINDOW_4);
        return m;
    }

    // Конфигурация окон и кнопок относительно верхнего левого угла окна в разных режимах
    // --- Арена ---
    public static final LinkedHashMap<String, Point> ARENA_BUTTONS = new LinkedHashMap<>() {{
        put("Клан - Выход", new Point(55,503));
        put("Арена", new Point(320,303));
        put("Атаковать", new Point(200,513));
        put("Пропустить", new Point(385,33));
        put("Закрыть — Победа", new Point(515,513));
        put("Закрыть — Поражение", new Point(515,463));
        put("Забрать коллекцию", new Point(515,463));
    }};

    // --- Клановая война ---
    public static final LinkedHashMap<String, Point> KV_BUTTONS = new LinkedHashMap<>() {{
        put("Клан", new Point(315,58));
        put("Война", new Point(55,303));
        put("Атаковать", new Point(415,288));
        put("Пропустить", new Point(385,33));
        put("Закрыть", new Point(515,433));
        put("Погон", new Point(565,523));
    }};

    // --- Рейд ---
    public static final LinkedHashMap<String, Point> RAID_BUTTONS = new LinkedHashMap<>() {{
        put("Клан", new Point(315,58));
        put("Рейды", new Point(55,413));
        put("Война", new Point(55,303));
        put("Обновить", new Point(110,38));
        put("Обновить2", new Point(525,43));
        put("Атаковать", new Point(424,323));
        put("Пропустить", new Point(385,33));
        put("Закрыть", new Point(515,433));
    }};

    // --- Туннель ---
    public static final LinkedHashMap<String, Point> TUNNEL_BUTTONS = new LinkedHashMap<>() {{
        put("В туннель", new Point(415,273));
        put("Карта-ПК-ФРУ", new Point(315,478));
        put("Карта-КОМ", new Point(315,428));
        put("Карта-УНИ", new Point(315,478));
        put("Карта-ПВ", new Point(315,533));
        put("Карта-КОМ-ФРУ", new Point(315,378));
        put("Карта-ФРУ-ПК", new Point(215,293));
        put("Войти с пропуском", new Point(275,373));
        put("Войти", new Point(275,303));
        put("Атаковать", new Point(424,323));
        put("Пропустить", new Point(385,33));
        put("Закрыть", new Point(515,513));
        put("Карта ПК-КРО", new Point(405,343));
        put("Карта КРО-ПК", new Point(195,393));
        put("Карта ПКк-ПКг", new Point(185,433));
        put("Карта ПКг-КИЕ", new Point(225,303));
        put("Карта КИЕ-ПКг", new Point(205,433));
        put("Карта ПКг-ПКк", new Point(185,383));
    }};








}
