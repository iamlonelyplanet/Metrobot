package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClanWarBot extends BaseBot {

    private final List<Integer> windows; // номера окон 1..4
    private final Map<Integer, WindowConfig.GameWindow> windowsMap;

    // Названия кнопок (должны совпадать с ключами в WindowConfig.KV_BUTTONS)
    private static final String BTN_CLAN = "КВ — Клан";
    private static final String BTN_WAR = "КВ — Война";
    private static final String BTN_ATTACK = "КВ — Атаковать";
    private static final String BTN_SKIP = "КВ — Пропустить бой";
    private static final String BTN_CLOSE = "КВ — Закрыть";

    // Тайминги
    private static final int TOTAL_BATTLES = 24;
    private static final long BETWEEN_WINDOWS_MS = 1000L;
    private static final long PAUSE_AFTER_CLAN_MS = 2000;
    private static final long PAUSE_AFTER_WAR_MS = 2000;
    private static final long PAUSE_AFTER_ATTACK_MS = 3000;
    private static final long PAUSE_AFTER_SKIP_MS = 3000;
    private static final long BETWEEN_BATTLES_MS = 293_000L;

    // Жёсткое время старта
    private static final int START_HOUR = 19;
    private static final int START_MIN = 55;

    public ClanWarBot(List<Integer> windows) throws Exception {
        this.windows = new ArrayList<>(windows);
        this.robot = new Robot();
        this.windowsMap = WindowConfig.defaultWindows();
    }

    public void start() {
        try {
            waitUntilStartTime();
            System.out.println("Старт КВ! Подготовь окна...");
            Thread.sleep(2000);

            // 1) Разовый вход в "Клан"
            clickAllWindows(WindowConfig.KV_BUTTONS, BTN_CLAN);
            Thread.sleep(PAUSE_AFTER_CLAN_MS);

            // 2) Разовый вход в "Войну"
            clickAllWindows(WindowConfig.KV_BUTTONS, BTN_WAR);
            Thread.sleep(PAUSE_AFTER_WAR_MS);

            // 3) 24 боя
            for (int battle = 1; battle <= TOTAL_BATTLES; battle++) {
                System.out.println("\n=== Бой №" + battle + " ===");

                clickAllWindows(WindowConfig.KV_BUTTONS, BTN_ATTACK);
                Thread.sleep(PAUSE_AFTER_ATTACK_MS);

                clickAllWindows(WindowConfig.KV_BUTTONS, BTN_SKIP);
                Thread.sleep(PAUSE_AFTER_SKIP_MS);

                clickAllWindows(WindowConfig.KV_BUTTONS, BTN_CLOSE);

                if (battle < TOTAL_BATTLES) {
                    countdown(BETWEEN_BATTLES_MS);
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

    private void waitUntilStartTime() throws InterruptedException {
        System.out.printf("Ожидание времени запуска: %02d:%02d...\n", START_HOUR, START_MIN);
        while (true) {
            LocalTime now = LocalTime.now();
            if (now.getHour() > START_HOUR) break;
            if (now.getHour() == START_HOUR && now.getMinute() >= START_MIN) break;
            Thread.sleep(1_000L);
        }
    }

    // Унифицированный метод: берёт координаты из WindowConfig и кликает по всем окнам
    private void clickAllWindows(Map<String, Point> buttons, String buttonName) throws InterruptedException {
        Point rel = buttons.get(buttonName);
        if (rel == null) {
            System.err.println("⚠ Кнопка \"" + buttonName + "\" не найдена в WindowConfig");
            return;
        }

        for (int i = 0; i < windows.size(); i++) {
            int idx = windows.get(i);
            WindowConfig.GameWindow gw = windowsMap.get(idx);
            if (gw == null) continue;

            int x = gw.topLeft.x + rel.x;
            int y = gw.topLeft.y + rel.y;
            clickAt(x, y);
            System.out.printf("%s нажал \"%s\" (%d,%d)\n", gw.name, buttonName, x, y);

            if (i < windows.size() - 1) Thread.sleep(BETWEEN_WINDOWS_MS);
        }
    }
}
