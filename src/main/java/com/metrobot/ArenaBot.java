package com.metrobot;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArenaBot extends BaseBot {

    private final List<Integer> windows; // номера окон 1..4
    private final Map<Integer, WindowConfig.GameWindow> windowsMap;

    // Верхние левые углы окон (фиксированные, как ты давал)
    private final Point[] windowPositions = new Point[]{new Point(0, 0),       // 1 — Ф1
            new Point(1033, 0),    // 2 — Лёха-156
            new Point(0, 670),     // 3 — Хуан
            new Point(1033, 670)   // 4 — Антон
    };

    // Названия окон
    private final String[] windowNames = new String[]{"Ф1", "Лёха-156", "Хуан", "Антон"};

    // Паузы и параметры
    private static final long PAUSE_AFTER_EACH_STEP_MS = 4000L; // 4 сек между этапами (Арена -> ... -> Закрыть)
    private static final long PAUSE_BETWEEN_WINDOWS_MS = 500L;  // 0.5 сек между окнами
    private static final int TOTAL_BATTLES = 50;
    private static final int INTER_BATTLE_SECONDS = 4 * 60 + 40; // 4 мин 40 сек

    public ArenaBot(List<Integer> windows) throws AWTException {
        this.windows = windows;
        this.robot = new Robot();
        this.windowsMap = WindowConfig.defaultWindows(); // 1..4 -> GameWindow (topLeft)
    }

    public void start() {
        try {
            System.out.println("Бот запустится через 5 секунд, подготовь окна игры...");
            countdownSeconds(5);

            for (int battle = 1; battle <= TOTAL_BATTLES; battle++) {
                System.out.println("\n=== Бой №" + battle + " ===");

                // порядок действий: все кнопки из WindowConfig
                for (Map.Entry<String, Point> btnEntry : WindowConfig.ARENA_BUTTONS.entrySet()) {
                    String buttonName = btnEntry.getKey();
                    clickAllWindows(buttonName);
                    Thread.sleep(PAUSE_AFTER_EACH_STEP_MS);
                }

                System.out.println("Бой " + battle + " завершён.");

                // Межбоевой таймер (если не последний бой)
                if (battle < TOTAL_BATTLES) {
                    System.out.println("Пауза между боями...");
                    countdown(INTER_BATTLE_SECONDS);
                }
            }

            System.out.println(TOTAL_BATTLES + "боёв завершены.");
        } catch (InterruptedException e) {
            System.out.println("Interrupted — завершаю.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Кликаем по всем выбранным окнам для данной кнопки
    private void clickAllWindows(String buttonName) throws InterruptedException {
        Point rel = WindowConfig.ARENA_BUTTONS.get(buttonName);
        if (rel == null) {
            System.err.println("⚠ Кнопка \"" + buttonName + "\" не найдена в WindowConfig");
            return;
        }

        for (int i = 0; i < windows.size(); i++) {
            int idx = windows.get(i);
            Point winPos = windowPositions[idx - 1];
            String winName = windowNames[idx - 1];

            int x = winPos.x + rel.x;
            int y = winPos.y + rel.y;
            clickAt(x, y);

            System.out.printf("[%s] Клик по кнопке \"%s\"%n", winName, buttonName);


            if (i < windows.size() - 1) Thread.sleep(PAUSE_BETWEEN_WINDOWS_MS);
        }
    }

    private void countdownSeconds(int seconds) throws InterruptedException {
        for (int i = seconds; i > 0; i--) {
            System.out.println(i + "...");
            Thread.sleep(1000);
        }
    }
}
