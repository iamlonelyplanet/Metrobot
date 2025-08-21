package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ClanWarBot extends BaseBot {

    private static final int  TOTAL_BATTLES = 24;
    private static final long PAUSE_AFTER_CLAN_MS = 2000;
    private static final long PAUSE_AFTER_WAR_MS  = 2000;
    private static final long PAUSE_AFTER_ATTACK_MS = 3000;
    private static final long PAUSE_AFTER_SKIP_MS   = 3000;
    private static final long BETWEEN_BATTLES_SECONDS = 293; // 4:53

    private final LocalTime startTime;

    public ClanWarBot(List<Integer> windows, LocalTime timeHHmm) {
        super(windows);
        this.startTime = timeHHmm;
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return WindowConfig.KV_BUTTONS;
    }

    @Override
    protected long betweenWindowsMs() { // в КВ у тебя была пауза 1000 мс
        return 1000L;
    }

    public void start() {
        try {
            waitUntilStartTime();
            System.out.printf("Старт КВ в %02d:%02d%n", startTime.getHour(), startTime.getMinute());
            Thread.sleep(2000);

            clickAllWindows("КВ — Клан");
            Thread.sleep(PAUSE_AFTER_CLAN_MS);

            clickAllWindows("КВ — Война");
            Thread.sleep(PAUSE_AFTER_WAR_MS);

            for (int battle = 1; battle <= TOTAL_BATTLES; battle++) {
                System.out.println("\n=== Бой №" + battle + " ===");

                clickAllWindows("КВ — Атаковать");
                Thread.sleep(PAUSE_AFTER_ATTACK_MS);

                clickAllWindows("КВ — Пропустить бой");
                Thread.sleep(PAUSE_AFTER_SKIP_MS);

                clickAllWindows("КВ — Закрыть");

                if (battle < TOTAL_BATTLES) {
                    countdown(BETWEEN_BATTLES_SECONDS);
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
