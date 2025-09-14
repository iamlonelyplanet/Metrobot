package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.metrobot.WindowConfig.*;

public class RaidBot extends BaseBot {
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
            System.out.println("Старт рейда");

            showAllGameWindows();
            Thread.sleep(PAUSE_LONG_MS);

            Map<String, Counter> counters = CounterStorage.loadCounters(Arrays.asList("Арена", "КВ", "Рейд"));
            Counter raidCounter = counters.get("Рейд");

            // Подготовительные клики (разово, если надо)
            if (raidCounter.getCount() == 0) {
                clickAllWindows("Клан");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Рейды");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Обновить");
                Thread.sleep(PAUSE_LONG_MS);
            }

            // Бои
            for (int battle = (raidCounter.getCount() + 1); battle <= MAX_BATTLES_RAID; battle++) {
                System.out.println("\n=== Бой №" + battle + " из " + MAX_BATTLES_RAID + " ===");
                showAllGameWindows();
                Thread.sleep(PAUSE_SHORT_MS);

                if (battle <= 2) {
                    clickAllWindows("Клан");
                    Thread.sleep(PAUSE_SHORT_MS);
                    clickAllWindows("Рейды");
                    Thread.sleep(PAUSE_LONG_MS);
                }
                clickAllWindows("Атаковать");
                Thread.sleep(PAUSE_BEFORE_BOSS_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Закрыть");
                Thread.sleep(PAUSE_SHORT_MS);

                raidCounter.plusOne();
                CounterStorage.saveCounters(counters);
                System.out.println(Grammar.getWordEnd(raidCounter.getCount()));

                if (battle < MAX_BATTLES_RAID) {
                    countdown(FIVE_MINUTES_PAUSE_SECONDS - 2);
                }
            }

            System.out.println("\nРейд завершён. Проведено боёв в автоматическом режиме: " + raidCounter.getCount());
        } catch (InterruptedException ie) {
            System.out.println("Прервано — выхожу.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
