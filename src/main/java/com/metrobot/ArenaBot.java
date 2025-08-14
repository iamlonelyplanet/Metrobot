package com.metrobot;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.*;
import java.util.List;

public class ArenaBot {
    private final List<Integer> windows; // номера окон 1..4
    private final Robot robot;

    // Верхние левые углы окон (фиксированные, как ты давал)
    private final Point[] windowPositions = new Point[]{
            new Point(0, 0),       // 1 — Ф1
            new Point(1033, 0),    // 2 — Лёха-156
            new Point(0, 670),     // 3 — Хуан
            new Point(1033, 670)   // 4 — Антон
    };

    // Названия окон
    private final String[] windowNames = new String[]{
            "Ф1", "Лёха-156", "Хуан", "Антон"
    };

    // Координаты кнопок (от верхнего левого угла окна)
    private final Map<String, Point> buttons = new LinkedHashMap<>();

    // Паузы
    private static final long PAUSE_AFTER_EACH_STEP_MS = 4000L; // 3 сек между этапами (Арена -> ... -> Закрыть)
    private static final long PAUSE_BETWEEN_WINDOWS_MS = 500L;  // 0.5 сек между окнами
    private static final int TOTAL_BATTLES = 50;
    private static final int INTER_BATTLE_SECONDS = 4 * 60 + 45; // 4 мин 45 сек

    public ArenaBot(List<Integer> windows) throws AWTException {
        this.windows = windows;
        this.robot = new Robot();

        buttons.put("Арена", new Point(430, 400));
        buttons.put("Атаковать", new Point(390, 610));
        buttons.put("Пропустить бой", new Point(510, 130));
        buttons.put("Закрыть — Победа", new Point(640, 615));
        buttons.put("Закрыть — Поражение", new Point(640, 560));
        buttons.put("Забрать коллекцию", new Point(640, 560));
    }

    public void start() {
        try {
            System.out.println("Бот запустится через 5 секунд, подготовь окна игры...");
            countdownSeconds(5);

            for (int battle = 1; battle <= TOTAL_BATTLES; battle++) {
                System.out.println("\n=== Бой №" + battle + " ===");

                // порядок действий: для каждой кнопки — кликаем по всем выбранным окнам (с паузой между окнами),
                // затем делаем общую паузу (2 с) перед следующей кнопкой
                for (Map.Entry<String, Point> btnEntry : buttons.entrySet()) {
                    String btnName = btnEntry.getKey();
                    Point btnRel = btnEntry.getValue();

                    clickAllSelectedWindows(btnRel, btnName);

                    // после кликов по всем окнам делаем паузу между этапами,
                    // но если это последняя кнопка ("Закрыть — Поражение"), можно также паузу держать
                    Thread.sleep(PAUSE_AFTER_EACH_STEP_MS);
                }

                System.out.println("Бой " + battle + " завершён.");

                // межбоевой таймер (если не последний бой)
                if (battle < TOTAL_BATTLES) {
                    System.out.println("Пауза между боями...");
                    runInterBattleCountdown(INTER_BATTLE_SECONDS);
                }
            }

            System.out.println("Все бои (до " + TOTAL_BATTLES + ") завершены.");
        } catch (InterruptedException e) {
            System.out.println("Interrupted — завершаю.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Кликаем по всем выбранным окнам для данной кнопки (с паузой 0.5 с между окнами)
    private void clickAllSelectedWindows(Point btnRelative, String btnName) throws InterruptedException {
        for (int i = 0; i < windows.size(); i++) {
            int winIndex = windows.get(i);
            if (winIndex < 1 || winIndex > windowPositions.length) continue; // защита
            Point winPos = windowPositions[winIndex - 1];
            String winName = windowNames[winIndex - 1];

            int x = winPos.x + btnRelative.x;
            int y = winPos.y + btnRelative.y;
            clickAt(x, y);

            System.out.printf("Клик в (%s) по кнопке \"%s\"%n", winName, btnName);

            if (i < windows.size() - 1) Thread.sleep(PAUSE_BETWEEN_WINDOWS_MS);
        }
    }

    private void clickAt(int x, int y) {
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    // Обратный отсчёт между боями — печать одной строкой, обновляемой каждую сек.
    private void runInterBattleCountdown(int totalSeconds) throws InterruptedException {
        for (int s = totalSeconds; s > 0; s--) {
            int mm = s / 60;
            int ss = s % 60;
            System.out.printf("\rОсталось до следующего боя: %02d:%02d   ", mm, ss);
            Thread.sleep(1000);
        }
        System.out.println(); // перевод строки после таймера
    }

    private void countdownSeconds(int seconds) throws InterruptedException {
        for (int i = seconds; i > 0; i--) {
            System.out.println(i + "...");
            Thread.sleep(1000);
        }
    }
}
