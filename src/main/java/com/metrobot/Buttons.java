package com.metrobot;

import java.awt.*;
import java.util.LinkedHashMap;

/**
 * Набор координат для кнопок, размеров окна и пауз между кликами.
 */

public class Buttons {
    // --- Паузы ---
    public static final int PAUSE_LONG_MS = 2800;
    public static final int PAUSE_SHORT_MS = 1200;
    public static final int PAUSE_SHORT_TUNNELS_MS = 600;
    public static final int PAUSE_MICRO_MS = 300;
    public static final int ATTACK_COOLDOWN_SECONDS = 301;
    public static final int PAUSE_RAID_BOSS_MS = 12_800;
    public static final int PAUSE_TUNNEL_MS = 16_000; // Для альтернативных скоростей: 16_000, 4_000, 8_000

    // --- Максимально допустимое количество боёв. Почему byte? Где ещё их использовать, если не в учёбе! ---
    public static final byte MAX_BATTLES_ARENA = 50;
    public static final byte MAX_BATTLES_CW = 24;
    public static final byte MAX_BATTLES_RAID = 12;
    public static final byte MAX_WAYS_TUNNEL = 5;
    public static final byte MAX_ENERGY = 30; // 50 при VIP

    /**
     * Координаты верхних левых углов рабочего поля внутри окна.
     * Площадь стянутого до минимума ("приведённого") окна Игроклуба: 1033x768, приводится автоматически с версии 1.1
     * Площадь "рабочего поля" в окне Игроклуба и ВК: 764×650; одинакова при всех разрешениях.
     * Ширина полосы прокрутки (элемента окон Windows): 19 в Игроклубе, собрать статистику в других разрешениях
     */
    public static final int WINDOW_WIDTH = 1033;
    public static final int  WINDOW_HEIGHT = 768;
    public static int xMoveRight = (WINDOW_WIDTH - 764 - 19) / 2; // Расчёт "нуля" (верхней левой точки) рабочего поля
    public static int yMoveDown = 97; // Константа для "Игроклуба" при WQHD, для остальных собирать статистику

    // === Координаты кнопок относительно верхнего левого угла "рабочего поля" (не окна!) ===
    // --- Арена ---
    public static final LinkedHashMap<String, Point> ARENA_BUTTONS = new LinkedHashMap<>() {{
        put("Клан - Выход", new Point(80, 505));
        put("Арена", new Point(328, 303)); //320, 303
        put("Атаковать", new Point(200, 513));
        put("Питомец", new Point(80, 505));
        put("Пропустить", new Point(385, 33));
        put("Закрыть — Победа", new Point(460, 520));
        put("Закрыть — Поражение", new Point(480, 463));
        put("Закрыть окно", new Point(510, 320));
    }};

    // --- Туннели ---
    public static final LinkedHashMap<String, Point> TUNNEL_BUTTONS = new LinkedHashMap<>() {{
        put("В туннель", new Point(415, 273));
        put("Войти", new Point(275, 303));
        put("Войти с пропуском", new Point(275, 373));
        put("Атаковать", new Point(425, 323));
        put("Пропустить", new Point(385, 33));
        put("Закрыть", new Point(460, 520));
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
        put("Питомец", new Point(80, 505));
        put("Закрыть окно", new Point(510, 320));
    }};

    // --- Крысы ---
    public static final LinkedHashMap<String, Point> RAT_BUTTONS = new LinkedHashMap<>() {{
        put("Клан - Выход", new Point(80, 505));
        put("Начстанции", new Point(100, 350));
        put("Крыса", new Point(465, 235));
        put("Пропустить", new Point(385, 33));
        put("Закрыть — Победа", new Point(460, 520));
        put("Закрыть — Поражение", new Point(480, 463));
        put("Питомец", new Point(80, 505));
        put("Закрыть окно", new Point(510, 320));
    }};

    // --- КВ и рейды ---
    public static final LinkedHashMap<String, Point> CLAN_BUTTONS = new LinkedHashMap<>() {{
        put("Клан", new Point(315, 58));
        put("Убрать автобой", new Point(145, 566));
        put("Арена - закрыть", new Point(515, 575));
        put("Война", new Point(80, 303));
        put("Атаковать врага", new Point(415, 288));
        put("Пропустить", new Point(385, 33));
        put("Закрыть", new Point(460, 433));
        put("Погон 1", new Point(480, 480));
        put("Погон 2", new Point(480, 525));
        put("Погон 3", new Point(480, 560));
        put("Погон - Коллекция", new Point(480, 465));
        put("Рейды", new Point(80, 413));
        put("Обновить", new Point(520, 40));
        put("Закрыть окно", new Point(510, 320));
        put("Атаковать босса", new Point(425, 323));
        put("Карта-левее", new Point(115, 270));
        put("Карта-ниже", new Point(420, 510));
        put("В рейд", new Point(170, 535));
        put("Зверь", new Point(435, 100)); // доступен с экрана 1
        put("Упырь", new Point(195, 120)); // доступен с экрана 1
        put("Вичуха", new Point(533, 450)); // доступен с экрана 1
        put("Стигмат", new Point(190, 435)); // доступен с экрана 2
        put("Горгон", new Point(440, 425)); // доступен с экрана 2
        put("Биомасса", new Point(360, 330)); // доступен с экрана 1
        put("Слизень", new Point(420, 460)); // доступен с экрана 1
        put("Тварь", new Point(185, 60)); // доступен с экрана 2
    }};
}