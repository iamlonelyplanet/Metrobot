package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.metrobot.WindowConfig.*;

public class RaidBot extends BaseBot {

    private static final int TOTAL_BATTLES = 12;
    private final LocalTime startTime;

    public RaidBot(List<Integer> windows, LocalTime timeHHmm) {
        super(windows);
        this.startTime = timeHHmm;
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return WindowConfig.RAID_BUTTONS;
    }

    public void start() {
        try {
            waitUntilStartTime(startTime);
            System.out.printf("Старт рейда в %02d:%02d%n", startTime.getHour(), startTime.getMinute());

            // Подготовительные клики (разово)
            showAllGameWindows();
            Thread.sleep(PAUSE_LONG_MS);

            clickAllWindows("Клан");
            Thread.sleep(PAUSE_SHORT_MS);
            clickAllWindows("Рейды");
            Thread.sleep(PAUSE_SHORT_MS);
            clickAllWindows("Обновить");
            Thread.sleep(PAUSE_LONG_MS);

            // Бои
            for (int battle = 1; battle <= TOTAL_BATTLES; battle++) {
                System.out.println("\n=== Рейд — бой #" + battle + " ===");
                showAllGameWindows();

                clickAllWindows("Рейды");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Атаковать");
                Thread.sleep(PAUSE_BEFORE_BOSS_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_SHORT_MS * 2);
                clickAllWindows("Закрыть");

                System.out.println("Бой " + battle + " завершён.");
                minimizeAllGameWindows();

                if (battle < TOTAL_BATTLES) {
                    countdown(FIVE_MINUTES_PAUSE_SECONDS);
                }
            }

            System.out.println("\nРейд завершён. Проведено боёв: " + TOTAL_BATTLES);
        } catch (InterruptedException ie) {
            System.out.println("Прервано пользователем.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
