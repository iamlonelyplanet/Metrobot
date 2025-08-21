package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class RaidBot extends BaseBot {

    private static final long PRE_START_DELAY_MS   = 5_000;   // 5 с
    private static final long PREP_STEP_DELAY_MS   = 3_000;   // 3 с
    private static final long IN_BATTLE_DELAY_MS   = 12_500;  // 12.5 с
    private static final long BETWEEN_BATTLES_MS   = 283_000; // 4:43
    private static final int  MAX_BATTLES = 12;

    private final LocalTime startTime;

    public RaidBot(List<Integer> windows, LocalTime timeHHmm) {
        super(windows);
        this.startTime = timeHHmm;
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return WindowConfig.RAID_BUTTONS;
    }

    // между окнами — оставить дефолт 500 мс

    public void start() {
        try {
            waitUntilStartTime();
            System.out.printf("Старт рейда в %02d:%02d%n", startTime.getHour(), startTime.getMinute());

            // Подготовительные клики (разово)
            Thread.sleep(PRE_START_DELAY_MS);
            clickAllWindows("Клан");     Thread.sleep(PREP_STEP_DELAY_MS);
            clickAllWindows("Рейды");    Thread.sleep(PREP_STEP_DELAY_MS);
            clickAllWindows("Обновить"); // если нужно — оставь, если нет, закомментируй

            // Бои
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
}
