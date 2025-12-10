package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.sun.jna.platform.win32.WinDef.HWND;

import static com.metrobot.Buttons.*;

/**
 * Унификация старых классов Raid и War
 * Режим "Клановые войны": бои перса в коллективной ("клановой") движухе.
 * Вручную занимало у пользователей порядка 2 часов (КВ) и часа (рейд), раз в 5 минут требуя внимания, притом сильно:
 * коллектив же.
 * <p>
 * Полное прохождение: то же время, полностью автоматически.
 * В режимах работает silent mode: окна разворачиваются перед серией кликов, затем сворачиваются обратно.
 * Повседневная работа пользователя в Windows прерывается раз в 5 минут всего на 10-12 секунд.
 * Счётчик боёв записывается в файл.
 * Приличное количество методов для всех классов-ботов унифицировано и вынесено в родительский BaseBot.
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
        return CLAN_BUTTONS;
    }

    private int totalBattles;
    private int lastSecondsCountdown;
    private int hoursToAdd;
    private boolean isGameGoingOn;
    private LocalTime endTime;

    public void start() {
        try {
            startGame();
            if (Objects.equals(botName, "Рейд")) {
                hoursToAdd = 1;
                totalBattles = MAX_BATTLES_RAID;
                lastSecondsCountdown = 6;
            }

            if (Objects.equals(botName, "КВ")) {
                hoursToAdd = 2;
                totalBattles = MAX_BATTLES_CW;
                lastSecondsCountdown = 3;
            }
            endTime = startTime.plusHours(hoursToAdd);
            endTime = endTime.minusSeconds(FIVE_MINUTES_PAUSE_SECONDS); // проверить
            isGameGoingOn = LocalTime.now().isBefore(endTime) && (unifiedCounter.getCount() < totalBattles);

            if (Objects.equals(botName, "Рейд")) {
                // Подготовительные клики (разово, если надо)
                if (unifiedCounter.getCount() == 0) {
                    showActiveWindows();
                    clickButton("Клан");
                    clickButton("Война");
                    clickButton("Обновить");
                    clickButton("Рейды");
                    Thread.sleep(PAUSE_SHORT_MS);
                }
                while (isGameGoingOn) {
                    fightInClan(BotType.RAID);
                }
            }

            if (Objects.equals(botName, "КВ")) {
                while (isGameGoingOn) {
                    fightInClan(BotType.CW);
                }
            }

            endGame();
        } catch (Exception e) {
            handleExceptions(e);
        }
    }

    public void fightInClan(BotType type) throws InterruptedException {
        if (type == BotType.CW) {
            System.out.println("\n=== Бой " + (unifiedCounter.getCount() + 1) + " из " + totalBattles + " ===");
            showActiveWindows();
            clickButton("Клан");
            clickButton("Война");
            clickButton("Атаковать врага");
            clickButton("Пропустить");
            clickButton("Закрыть");
            clickButton("Погон");
            clickButton("Погон 2");
            clickButton("Погон 3");
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
            clickButton("Закрыть");
            minimizeActiveWindows();
        }

        unifiedCounter.plusOne();
        CounterStorage.saveCounters(counters);
        System.out.println(Grammar.getWordEnd(unifiedCounter.getCount()));
        isGameGoingOn = LocalTime.now().isBefore(endTime) && (unifiedCounter.getCount() < totalBattles);
        if (isGameGoingOn) {
            countdown(FIVE_MINUTES_PAUSE_SECONDS - activeWindows.size() - lastSecondsCountdown);
        }
    }
}
