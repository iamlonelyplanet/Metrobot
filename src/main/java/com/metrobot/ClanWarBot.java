package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.metrobot.WindowConfig.*;

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
            waitUntilStartTime(startTime);
            System.out.printf("Старт КВ в %02d:%02d%n", startTime.getHour(), startTime.getMinute());
            Thread.sleep(2000);

            showAllGameWindows();
            clickAllWindows("КВ — Клан");

            for (int battle = 1; battle <= TOTAL_BATTLES; battle++) {
                System.out.println("\n=== Бой №" + battle + " ===");
                showAllGameWindows();
                Thread.sleep(PAUSE_SHORT_MS * 2);

                clickAllWindows("КВ — Война");
                clickAllWindows("Атаковать");
                clickAllWindows("Пропустить");
                clickAllWindows("Закрыть");
                clickAllWindows("КВ — Погон");

                System.out.println("Бой " + battle + " завершён.");
                minimizeAllGameWindows();

                if (battle < TOTAL_BATTLES) {
                    countdown(FIVE_MINUTES_PAUSE_SECONDS + 6);
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
}
