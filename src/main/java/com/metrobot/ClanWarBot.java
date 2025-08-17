package com.metrobot;

import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClanWarBot {

    private final List<Integer> windows; // номера окон 1..4
    private final Robot robot;
    private final Map<Integer, WindowConfig.GameWindow> windowsMap;

    // Названия кнопок (ровно как в WindowConfig.KV_BUTTONS)
    private static final String BTN_CLAN   = "КВ — Клан";
    private static final String BTN_WAR    = "КВ — Война";
    private static final String BTN_ATTACK = "КВ — Атаковать";
    private static final String BTN_SKIP   = "КВ — Пропустить бой";
    private static final String BTN_CLOSE  = "КВ — Закрыть";

    // Тайминги
    private static final int TOTAL_BATTLES = 24;          // всего боёв
    private static final long BETWEEN_WINDOWS_MS = 500L;  // пауза между окнами
    private static final long PAUSE_AFTER_CLAN_MS = 2000; // после нажатия "Клан" (разово)
    private static final long PAUSE_AFTER_WAR_MS = 1000;  // после "Война"
    private static final long PAUSE_AFTER_ATTACK_MS = 2000; // после "Атаковать"
    private static final long PAUSE_AFTER_SKIP_MS = 2000; // после "Пропустить бой"
    private static final long BETWEEN_BATTLES_MS = 292_000L; // 4 мин 52 сек

    // Жёсткое время старта
    private static final int START_HOUR = 16;
    private static final int START_MIN = 7;

    public ClanWarBot(List<Integer> windows) throws Exception {
        this.windows = new ArrayList<>(windows);
        this.robot = new Robot();
        this.windowsMap = WindowConfig.defaultWindows(); // 1..4 -> GameWindow (topLeft)
    }

    public void start() {
        try {
            waitUntilStartTime();
            System.out.println("Старт КВ! Подготовь окна...");

            // 1) Разовый вход в "Клан"
            clickAllSelectedWindows(BTN_CLAN);
            Thread.sleep(PAUSE_AFTER_CLAN_MS);

            // 2) 24 боя
            for (int battle = 1; battle <= TOTAL_BATTLES; battle++) {
                System.out.println("\n=== Бой №" + battle + " ===");

                clickAllSelectedWindows(BTN_WAR);
                Thread.sleep(PAUSE_AFTER_WAR_MS);

                clickAllSelectedWindows(BTN_ATTACK);
                Thread.sleep(PAUSE_AFTER_ATTACK_MS);

                clickAllSelectedWindows(BTN_SKIP);
                Thread.sleep(PAUSE_AFTER_SKIP_MS);

                clickAllSelectedWindows(BTN_CLOSE);

                if (battle < TOTAL_BATTLES) {
                    System.out.println("Ожидание до следующего боя: 04:52...");
                    Thread.sleep(BETWEEN_BATTLES_MS);
                }
            }

            System.out.println("\nВсе " + TOTAL_BATTLES + " боёв завершены. КВ окончена.");
        } catch (InterruptedException ie) {
            System.out.println("Прервано — выхожу.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Кликает указанную кнопку по всем выбранным окнам с учётом смещения окна
    private void clickAllSelectedWindows(String buttonName) throws InterruptedException {
        Point rel = WindowConfig.KV_BUTTONS.get(buttonName);
        if (rel == null) {
            System.err.println("[ClanWarBot] Кнопка \"" + buttonName + "\" отсутствует в WindowConfig.KV_BUTTONS");
            return;
        }
        for (int i = 0; i < windows.size(); i++) {
            int idx = windows.get(i);
            WindowConfig.GameWindow gw = windowsMap.get(idx);
            if (gw == null) continue;

            int x = gw.topLeft.x + rel.x;
            int y = gw.topLeft.y + rel.y;
            clickAt(x, y);
            System.out.printf("Окно \"%s\": нажал \"%s\" (%d,%d)\n", gw.name, buttonName, x, y);

            if (i < windows.size() - 1) Thread.sleep(BETWEEN_WINDOWS_MS);
        }
    }

    private void clickAt(int x, int y) {
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    private void waitUntilStartTime() throws InterruptedException {
        System.out.printf("Ожидание времени запуска: %02d:%02d...\n", START_HOUR, START_MIN);
        while (true) {
            LocalTime now = LocalTime.now();
            if (now.getHour() > START_HOUR) break;
            if (now.getHour() == START_HOUR && now.getMinute() >= START_MIN) break;
            Thread.sleep(1_000L);
        }
    }
}
