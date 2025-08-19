package com.metrobot;

import java.awt.*;
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
    private static final long PREP_STEP_DELAY_MS = 3_000;      // пауза между шагами подготовки
    private static final long IN_BATTLE_DELAY_MS = 12_500;     // ожидание внутри боя
    private static final long BETWEEN_BATTLES_MS = 283_000;    // 4 мин 43 сек между боями
    private static final long BETWEEN_WINDOWS_MS = 500;        // пауза между окнами при одном шаге
    private static final int MAX_BATTLES = 12;

    // ===== Поля =====
    private final List<Integer> windows;                          // выбранные окна (1..4)
    private final Map<Integer, WindowConfig.GameWindow> windowsMap;   // 1..4 -> окно
    private final LocalTime startTime;                            // время старта рейда

    /**
     * Конструктор под твой Main: (windows, timeStr "HH:mm")
     */
    public RaidBot(List<Integer> windows, String timeHHmm) throws Exception {
        this.windows = new ArrayList<>(windows);
        this.windowsMap = WindowConfig.defaultWindows();
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
            clickAllWindows("Клан");
            Thread.sleep(PREP_STEP_DELAY_MS);
            clickAllWindows("Рейды");
            Thread.sleep(PREP_STEP_DELAY_MS);
            clickAllWindows("Обновить");

            // 12 боёв
            for (int battle = 1; battle <= MAX_BATTLES; battle++) {
                System.out.println("\n=== Рейд — бой #" + battle + " ===");

                Thread.sleep(PREP_STEP_DELAY_MS);
                clickAllWindows("Атаковать");

                Thread.sleep(IN_BATTLE_DELAY_MS);
                clickAllWindows("Пропустить");

                Thread.sleep(PREP_STEP_DELAY_MS);
                clickAllWindows("Закрыть");

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

    private void clickAllWindows(String buttonName) throws InterruptedException {
        Point rel = WindowConfig.RAID_BUTTONS.get(buttonName);
        if (rel == null) {
            System.err.println("⚠ Кнопка \"" + buttonName + "\" не найдена в WindowConfig");
            return;
        }

        for (int i = 0; i < windows.size(); i++) {
            int idx = windows.get(i);
            WindowConfig.GameWindow gw = windowsMap.get(idx);

            int x = gw.topLeft.x + rel.x;
            int y = gw.topLeft.y + rel.y;
            clickAt(x, y);
            System.out.printf("Окно \"%s\": \"%s\" (%d,%d)%n", gw.name, buttonName, x, y);

            if (i < windows.size() - 1) Thread.sleep(BETWEEN_WINDOWS_MS);
        }
    }
}
