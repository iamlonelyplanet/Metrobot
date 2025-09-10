package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.metrobot.WindowConfig.*;

public class ArenaBot extends BaseBot {

    private static final int TOTAL_BATTLES = 50;
    private final LocalTime startTime;

    public ArenaBot(List<Integer> windows, LocalTime timeHHmm) {
        super(windows);
        this.startTime = timeHHmm;
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return WindowConfig.ARENA_BUTTONS;
    }

    public void start() {
        try {
            waitUntilStartTime(startTime);
            System.out.printf("Старт Арены в %02d:%02d%n", startTime.getHour(), startTime.getMinute());
            Thread.sleep(PAUSE_SHORT_MS);
            Counter arena = new Counter("Арена");
            int battlesLeft = TOTAL_BATTLES - arena.getCount();

            for (int battle = 1; battle <= battlesLeft; battle++) {
                System.out.println("\n=== Бой №" + battle + " из " + battlesLeft + " ===");

                showAllGameWindows();
                clickAllWindows("Арена");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Атаковать");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Закрыть — Победа");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Закрыть — Поражение");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Забрать коллекцию");
                Thread.sleep(PAUSE_SHORT_MS);

                System.out.println("Бой " + battle + " завершён.");
                minimizeAllGameWindows();

                arena.plusOne();

                if (battle < battlesLeft) {
                    System.out.println("Пауза между боями...");
                    countdown(FIVE_MINUTES_PAUSE_SECONDS + 3);
                }
            }

            System.out.println(TOTAL_BATTLES + " боёв завершено.");
        } catch (InterruptedException e) {
            System.out.println("Interrupted — завершаю.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
