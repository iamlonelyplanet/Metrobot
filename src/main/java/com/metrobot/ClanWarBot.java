package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.Arrays;
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
            System.out.println("Старт КВ");
            Thread.sleep(PAUSE_SHORT_MS);

            Map<String, Counter> counters = CounterStorage.loadCounters(Arrays.asList("Арена", "КВ", "Рейд"));
            Counter kvCounter = counters.get("КВ");

            // Бои
            for (int battle = (kvCounter.getCount() + 1); battle <= MAX_BATTLES_CLANWAR; battle++) {
                System.out.println("\n=== Бой №" + battle + " из " + MAX_BATTLES_CLANWAR + " ===");
                showAllGameWindows();

                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Клан");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Война");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("Атаковать");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Закрыть");
                Thread.sleep(PAUSE_SHORT_MS);
                clickAllWindows("КВ — Погон");
                Thread.sleep(PAUSE_SHORT_MS);
                minimizeAllGameWindows();

                kvCounter.plusOne();
                CounterStorage.saveCounters(counters);
                System.out.println(Grammar.getWordEnd(kvCounter.getCount()));

                if (battle < MAX_BATTLES_CLANWAR) {
                    countdown(FIVE_MINUTES_PAUSE_SECONDS);
                }
            }

            System.out.println("\nКВ завершена. Проведено боёв в автоматическом режиме: " + kvCounter.getCount());
        } catch (InterruptedException ie) {
            System.out.println("Прервано — выхожу.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
