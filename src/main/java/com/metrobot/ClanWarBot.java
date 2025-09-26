package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.metrobot.Buttons.*;

public class ClanWarBot extends BaseBot {
    public ClanWarBot(List<Integer> windows, LocalTime timeHHmm, String botName) {
        super(windows);
        this.startTime = timeHHmm;
        this.botName = botName;
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return Buttons.KV_BUTTONS;
    }

    public void start() {
        try {
            startGame();

            // Бои
            for (int battle = (unificatedCounter.getCount() + 1); battle <= MAX_BATTLES_CLANWAR; battle++) {
                System.out.println("\n=== Бой №" + battle + " из " + MAX_BATTLES_CLANWAR + " ===");
                showAllGameWindows();
                clickAllWindows("Клан");
                clickAllWindows("Война");
                clickAllWindows("Атаковать");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Закрыть");
                clickAllWindows("Погон");
                minimizeAllGameWindows();

                unificatedCounter.plusOne();
                CounterStorage.saveCounters(counters);
                System.out.println(Grammar.getWordEnd(unificatedCounter.getCount()));
                if (battle < MAX_BATTLES_CLANWAR) {
                    countdown(FIVE_MINUTES_PAUSE_SECONDS - activeWindows.size());
                }
            }
            endGame();
        } catch (Exception e) {
            handleExceptions(e);
        }
    }
}
