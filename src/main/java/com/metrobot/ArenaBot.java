package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.metrobot.WindowConfig.*;

public class ArenaBot extends BaseBot {
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
            System.out.print("Старт Арены");
            Thread.sleep(PAUSE_SHORT_MS);

            Map<String, Counter> counters = CounterStorage.loadCounters(Arrays.asList("Арена", "КВ", "Рейд"));
            Counter arenaCounter = counters.get("Арена");

            int battlesLeft = MAX_BATTLES_ARENA - arenaCounter.getCount();

            for (int battle = (arenaCounter.getCount() + 1); battle <= battlesLeft; battle++) {
                System.out.println("\n=== Бой №" + battle + " из " + MAX_BATTLES_ARENA + " ===");
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

                arenaCounter.plusOne();
                CounterStorage.saveCounters(counters);
                System.out.println("Прошло " + arenaCounter.getCount() + " боёв");

                if (battle <= MAX_BATTLES_ARENA) {
                    System.out.println("Пауза между боями...");
                    countdown(FIVE_MINUTES_PAUSE_SECONDS + 3);
                }
            }

            System.out.println(MAX_BATTLES_ARENA + " боёв завершено.");

        } catch (InterruptedException e) {
            System.out.println("Interrupted — завершаю.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
