package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.metrobot.WindowConfig.*;

public class ClanWarBot extends BaseBot {

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
            Thread.sleep(PAUSE_SHORT_MS);

            showAllGameWindows();
            // Однократный подготовительный клик
            clickAllWindows("КВ — Клан");

            for (int battle = 1; battle <= MAX_BATTLES_CLANWAR; battle++) {
                System.out.println("\n=== Бой №" + battle + " ===");
                showAllGameWindows();
                Thread.sleep(PAUSE_SHORT_MS);

                clickAllWindows("КВ — Война");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Атаковать");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_SHORT_MS * 2);
                clickAllWindows("Закрыть");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("КВ — Погон");
                Thread.sleep(PAUSE_SHORT_MS);

                System.out.println("Бой " + battle + " завершён.");
                minimizeAllGameWindows();

                if (battle < MAX_BATTLES_CLANWAR) {
                    countdown(FIVE_MINUTES_PAUSE_SECONDS + 5);
                }
            }

            System.out.println("\nВсе " + MAX_BATTLES_CLANWAR + " боёв завершены. КВ окончена.");
        } catch (InterruptedException ie) {
            System.out.println("Прервано — выхожу.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
