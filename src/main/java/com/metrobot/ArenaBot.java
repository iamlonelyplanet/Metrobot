package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.metrobot.Buttons.*;

public class ArenaBot extends BaseBot {
    public ArenaBot(List<Integer> windows, LocalTime timeHHmm, String botName) {
        super(windows);
        this.startTime = timeHHmm;
        this.botName = botName;
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return Buttons.ARENA_BUTTONS;
    }

    public void start() {
        try {
            startGame();

            // Бои
            for (int battle = (unificatedCounter.getCount() + 1); battle <= MAX_BATTLES_ARENA; battle++) {
                System.out.println("\n=== Бой №" + battle + " из " + MAX_BATTLES_ARENA + " ===");
                showAllGameWindows();
                clickAllWindows("Клан - Выход");
                clickAllWindows("Арена");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Атаковать");
                Thread.sleep(PAUSE_LONG_MS);
//                clickAllWindows("Питомец"); // Опционально. Можно сделать целый отдельный режим игры!
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Закрыть — Победа");
                clickAllWindows("Закрыть — Поражение");
                clickAllWindows("Забрать коллекцию");
                minimizeAllGameWindows();

                unificatedCounter.plusOne();
                CounterStorage.saveCounters(counters);
                System.out.println(Grammar.getWordEnd(unificatedCounter.getCount()));

                if (battle < MAX_BATTLES_ARENA) {
                    countdown(FIVE_MINUTES_PAUSE_SECONDS - activeWindows.size());
                }
            }
            endGame();
        } catch (Exception e) {
            handleExceptions(e);
        }
    }
}
