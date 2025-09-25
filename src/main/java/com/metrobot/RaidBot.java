package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.metrobot.Buttons.*;

public class RaidBot extends BaseBot {

    public RaidBot(List<Integer> windows, LocalTime timeHHmm, String botName) {
        super(windows);
        this.startTime = timeHHmm;
        this.botName = botName;
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return Buttons.RAID_BUTTONS;
    }

    public void start() {
        try {
            startGame();
            Counter raidCounter = counters.get(botName);

            // Подготовительные клики (разово, если надо)
            if (raidCounter.getCount() == 0) {
                showAllGameWindows();
                clickAllWindows("Клан");
                clickAllWindows("Война");
                clickAllWindows("Обновить");
                clickAllWindows("Рейды");
                Thread.sleep(PAUSE_LONG_MS);
            }

            // Бои
            for (int battle = (raidCounter.getCount() + 1); battle <= MAX_BATTLES_RAID; battle++) {
                System.out.println("\n=== Бой №" + battle + " из " + MAX_BATTLES_RAID + " ===");
                showAllGameWindows();
                Thread.sleep(PAUSE_SHORT_MS);

                clickAllWindows("Клан");
                clickAllWindows("Рейды");
                clickAllWindows("Атаковать");
                Thread.sleep(PAUSE_BEFORE_BOSS_MS);
                clickAllWindows("Пропустить");
                Thread.sleep(PAUSE_LONG_MS);
                clickAllWindows("Закрыть");
                minimizeAllGameWindows();

                raidCounter.plusOne();
                CounterStorage.saveCounters(counters);
                System.out.println(Grammar.getWordEnd(raidCounter.getCount()));

                if (battle < MAX_BATTLES_RAID) {
                    countdown(FIVE_MINUTES_PAUSE_SECONDS - activeWindows.size() - 3);
                }
            }

            System.out.println("\nРежим " + botName + " завершён. " +
                    "Проведено боёв в автоматическом режиме: " + raidCounter.getCount());
        } catch (InterruptedException ie) {
            System.out.println("Прервано — выхожу.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
