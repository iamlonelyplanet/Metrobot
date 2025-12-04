package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.sun.jna.platform.win32.WinDef.HWND;

import static com.metrobot.Buttons.*;

/**
 * Унификация классов RaidBot и ClanWarBot
 */

public class ClanBot extends BaseBot {

    public ClanBot(List<HWND> windows, LocalTime timeHHmm, String botName) throws AWTException {
        super(windows);
        {
            this.startTime = timeHHmm;
            this.botName = botName;
        }
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return Buttons.ALL_BUTTONS;
    }

    public void start() {
        try {
            startGame();
            int hoursToAdd;
            int totalBattles;
            if (Objects.equals(botName, "Рейд")) {
                hoursToAdd = 1;
                totalBattles = MAX_BATTLES_RAID;
            } else {
                hoursToAdd = 2;
                totalBattles = MAX_BATTLES_CW;
            }
            LocalTime endTime = startTime.plusHours(hoursToAdd);
            endTime = endTime.minusSeconds(FIVE_MINUTES_PAUSE_SECONDS); // проверить
            boolean isGameGoingOn = LocalTime.now().isBefore(endTime) && (unifiedCounter.getCount() < totalBattles);

            // Бои в рейде
            if (Objects.equals(botName, "Рейд")) {
                // Подготовительные клики (разово, если надо)
                if (unifiedCounter.getCount() == 0) {
                    showActiveWindows();
                    clickButton("Клан");
                    clickButton("Война");
                    clickButton("Обновить");
                    Thread.sleep(PAUSE_LONG_MS);
                    clickButton("Рейды");
                    Thread.sleep(PAUSE_SHORT_MS);
                }

                while (isGameGoingOn) {
                    fightWithClan(BotType.RAID, totalBattles);
                    isGameGoingOn = LocalTime.now().isBefore(endTime) && (unifiedCounter.getCount() < totalBattles);
                    if (isGameGoingOn) {
                        countdown(FIVE_MINUTES_PAUSE_SECONDS - activeWindows.size() - 6);
                    }
                }
            }

            // Бои КВ
            if (Objects.equals(botName, "КВ")) {
                while (isGameGoingOn) {
                    fightWithClan(BotType.CW, totalBattles);
                    isGameGoingOn = LocalTime.now().isBefore(endTime) && (unifiedCounter.getCount() < totalBattles);
                    if (isGameGoingOn) {
                        countdown(FIVE_MINUTES_PAUSE_SECONDS - activeWindows.size() - 1);
                    }
                }
            }

            endGame();
        } catch (Exception e) {
            handleExceptions(e);
        }
    }

    public void fightWithClan(BotType type, int totalBattles) throws InterruptedException {
        if (type == BotType.CW) {
            System.out.println("\n=== Бой " + (unifiedCounter.getCount() + 1) + " из " + totalBattles + " ===");
            showActiveWindows();
            clickButton("Клан");
            clickButton("Война");
            clickButton("Атаковать врага");
            Thread.sleep(PAUSE_LONG_MS);
            clickButton("Пропустить");
            Thread.sleep(PAUSE_LONG_MS);
            clickButton("Закрыть");
            clickButton("Погон");
            clickButton("Погон 2");
            clickButton("Погон - Коллекция");
            minimizeActiveWindows();
        }
        if (type == BotType.RAID) {
            System.out.println("\n=== Бой " + (unifiedCounter.getCount() + 1) + " из " + totalBattles + " ===");
            showActiveWindows();
            Thread.sleep(PAUSE_SHORT_MS);

            if (unifiedCounter.getCount() != 0) {
                clickButton("Клан");
                clickButton("Рейды");
            }

            clickButton("Атаковать босса");
            Thread.sleep(PAUSE_RAID_BOSS_MS);
            clickButton("Пропустить");
            Thread.sleep(PAUSE_LONG_MS);
            clickButton("Закрыть");
            minimizeActiveWindows();
        }
        unifiedCounter.plusOne();
        CounterStorage.saveCounters(counters);
        System.out.println(Grammar.getWordEnd(unifiedCounter.getCount()));
    }
}
