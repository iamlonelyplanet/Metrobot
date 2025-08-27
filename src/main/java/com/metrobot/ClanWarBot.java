package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.metrobot.WindowConfig.FIVE_MINUTES_PAUSE_SECONDS;
import static com.metrobot.WindowConfig.PAUSE_MS;

public class ClanWarBot extends BaseBot {

    private static final int TOTAL_BATTLES = 24;

    private final LocalTime startTime;

    public ClanWarBot(List<Integer> windows, LocalTime timeHHmm) {
        super(windows);
        this.startTime = timeHHmm;
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return WindowConfig.KV_BUTTONS;
    }

    public void start() {
        try {
            waitUntilStartTime();
            System.out.printf("Старт КВ в %02d:%02d%n", startTime.getHour(), startTime.getMinute());
            Thread.sleep(2000);

            showAllGameWindows();

            clickAllWindows("КВ — Клан");
            Thread.sleep(PAUSE_MS);

            clickAllWindows("КВ — Война");
            Thread.sleep(PAUSE_MS);

            for (int battle = 1; battle <= TOTAL_BATTLES; battle++) {
                System.out.println("\n=== Бой №" + battle + " ===");
                showAllGameWindows();

                clickAllWindows("КВ — Атаковать");
                Thread.sleep(PAUSE_MS);

                clickAllWindows("КВ — Пропустить бой");
                Thread.sleep(PAUSE_MS);

                clickAllWindows("КВ — Закрыть");

                minimizeAllGameWindows();
                if (battle < TOTAL_BATTLES) {
                    countdown(FIVE_MINUTES_PAUSE_SECONDS + 8);
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
        System.out.printf("Ожидание времени запуска КВ: %02d:%02d...\n",
                startTime.getHour(), startTime.getMinute());
        while (true) {
            LocalTime now = LocalTime.now();
            if (now.getHour() > startTime.getHour()) break;
            if (now.getHour() == startTime.getHour() && now.getMinute() >= startTime.getMinute()) break;
            Thread.sleep(1_000L);
        }
    }
}
