package com.metrobot;

import java.awt.*;
import java.awt.event.InputEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Режим "Рейд".
 * - Старт по времени (HH:mm), которое ты вводишь в Main.
 * - Через 5 секунд после старта: "Клан" -> (1с) -> "Рейды" -> (1с) -> "Обновить".
 * - Затем 12 боёв: каждые 5 минут. Внутри боя задержки по 4 секунды между кликами.
 * - Клики выполняются по всем выбранным окнам (1..4) с учётом их смещений из WindowConfig.
 */
public class RaidBot extends BaseBot {

    // ===== Настройки времени =====
    private static final long PRE_START_DELAY_MS = 5_000;      // 5 секунд перед подготовительными кликами
    private static final long PREP_STEP_DELAY_MS = 3_000;      // 2 сек между кнопками подготовки
    private static final long IN_BATTLE_DELAY_MS = 12_500;      // 12,5 сек между кликами внутри боя
    private static final long BETWEEN_BATTLES_MS = 283_000;    // 4 мин 43 сек между боями
    private static final long BETWEEN_WINDOWS_MS = 500;        // пауза между окнами при одном шаге
    private static final int MAX_BATTLES = 12;

    // ===== Координаты кнопок (относительно верхнего левого угла окна) =====
    private static final Point BTN_CLAN = new Point(290, 160);
    private static final Point BTN_RAIDS = new Point(180, 510);
    private static final Point BTN_REFRESH = new Point(240, 135);

    private static final Point BTN_ATTACK = new Point(549, 430);
    private static final Point BTN_SKIP = new Point(510, 120);
    private static final Point BTN_CLOSE = new Point(640, 530);

    // ===== Поля =====
    private final List<Integer> windows;                          // выбранные окна (1..4)
    private final Map<Integer, WindowConfig.GameWindow> winMap;   // 1..4 -> окно
    private final LocalTime startTime;                            // время старта рейда

    /**
     * Конструктор под твой Main: (windows, timeStr "HH:mm")
     */
    public RaidBot(List<Integer> windows, String timeHHmm) throws Exception {
        this.windows = new ArrayList<>(windows);
        this.winMap = WindowConfig.defaultWindows();
        this.startTime = LocalTime.parse(timeHHmm, DateTimeFormatter.ofPattern("H:mm"));
        this.robot = new Robot();
    }

    /**
     * Запуск рейда (блокирующий). Совпадает по имени с вызовом в Main: bot.start();
     */
    public void start() {
        try {
            waitUntilStartTime();
            System.out.printf("Старт рейда в %02d:%02d%n", startTime.getHour(), startTime.getMinute());


            // Подготовительные клики (разово)
            Thread.sleep(PRE_START_DELAY_MS);
            /** clickAllWindows(BTN_CLAN, "Клан");
            Thread.sleep(PREP_STEP_DELAY_MS);
            clickAllWindows(BTN_RAIDS, "Рейды");
            Thread.sleep(PREP_STEP_DELAY_MS);
            clickAllWindows(BTN_REFRESH, "Обновить");
             */

            // 12 боёв
            for (int battle = 1; battle <= MAX_BATTLES; battle++) {
                System.out.println("\n=== Рейд — бой #" + battle + " ===");

                Thread.sleep(PREP_STEP_DELAY_MS);
                clickAllWindows(BTN_ATTACK, "Атаковать");

                Thread.sleep(IN_BATTLE_DELAY_MS);
                clickAllWindows(BTN_SKIP, "Пропустить");

                Thread.sleep(PREP_STEP_DELAY_MS);
                clickAllWindows(BTN_CLOSE, "Закрыть");

                if (battle < MAX_BATTLES) {
                    System.out.println("Ожидание 4:46 до следующего боя...");
                    countdown(BETWEEN_BATTLES_MS / 1000);
                }
            }

            System.out.println("\nРейд завершён. Проведено боёв: " + MAX_BATTLES);
        } catch (InterruptedException ie) {
            System.out.println("Прервано пользователем.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== Служебные методы =====

    private void waitUntilStartTime() throws InterruptedException {
        System.out.printf("Ожидание времени запуска рейда: %02d:%02d...%n",
                startTime.getHour(), startTime.getMinute());
        while (true) {
            LocalTime now = LocalTime.now();
            if (now.getHour() > startTime.getHour()) break;
            if (now.getHour() == startTime.getHour() && now.getMinute() >= startTime.getMinute()) break;
            Thread.sleep(1000);
        }
    }

    private void clickAllWindows(Point rel, String label) throws InterruptedException {
        for (int i = 0; i < windows.size(); i++) {
            int idx = windows.get(i);
            WindowConfig.GameWindow gw = winMap.get(idx);
            if (gw == null) continue;

            int x = gw.topLeft.x + rel.x;
            int y = gw.topLeft.y + rel.y;
            clickAt(x, y);
            System.out.printf("Окно \"%s\": \"%s\" (%d,%d)%n", gw.name, label, x, y);

            if (i < windows.size() - 1) Thread.sleep(BETWEEN_WINDOWS_MS);
        }
    }
}
