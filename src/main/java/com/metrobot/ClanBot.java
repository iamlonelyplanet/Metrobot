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
 * Режим "Клан": бои перса в коллективной ("клановой") движухе.
 * В режиме "Клан" работает silent mode: окна разворачиваются перед серией кликов, затем сворачиваются обратно.
 * Вручную занимал у пользователей порядка 2 часов (КВ) и часа (рейд), раз в 5 минут требуя внимания, притом сильно:
 * коллектив же.
 * <p>
 * Полное прохождение: то же время, полностью автоматически.
 * В этом режиме работает silent mode: окна разворачиваются перед серией кликов, затем сворачиваются обратно.
 * Повседневная работа пользователя в Windows прерывается всего на 10-12 секунд раз в 5 минут.
 * Счётчик боёв записывается в файл.
 */

public class ClanBot extends BaseBot {
    public ClanBot(List<HWND> windows,
                   LocalTime timeHHmm,
                   String botName,
                   boolean closeAfterFinish) throws AWTException {

        super(windows);

        {
            this.startTime = timeHHmm;
            this.botName = botName;
            this.windows = windows;
            this.closeAfterFinish = closeAfterFinish;
        }
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return CLAN_BUTTONS;
    }
    protected final List<HWND> windows;
    private int totalBattles;
    private int lastSecondsCorrection;
    private boolean isGameGoingOn;
    private LocalTime endTime;

    public void start() {
        try {
            startGame();
            int hoursToFinish;
            if (Objects.equals(botName, "Рейд")) {
                hoursToFinish = 1;
                totalBattles = MAX_BATTLES_RAID;
                lastSecondsCorrection = 4;
            } else {
                hoursToFinish = 2;
                totalBattles = MAX_BATTLES_CW;
                lastSecondsCorrection = 4;
            }

            endTime = startTime.plusHours(hoursToFinish);
            endTime = endTime.minusSeconds(FIVE_MINUTES_PAUSE_SECONDS + lastSecondsCorrection);
            isGameGoingOn = LocalTime.now().isBefore(endTime) && (unifiedCounter.getCount() < totalBattles);

            if (Objects.equals(botName, "Рейд")) {
                // Подготовительные клики (однократно, перед первым боем рейда)
                if (unifiedCounter.getCount() == 0) {
                    showActiveWindows();
                    clickButton("Клан");
                    clickButton("Война");
                    clickButton("Обновить");
                    clickButton("Рейды");
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
        printBattleNumber((unifiedCounter.getCount() + 1), totalBattles);
        if (unifiedCounter.getCount() == 0) {
            clickButton("Убрать автобой");
            clickButton("Арена - закрыть");
        }

        if (type == BotType.CW) {
            clickButton("Клан");
            clickButton("Война");
            clickButton("Атаковать врага");
            clickButton("Пропустить");
            clickButton("Закрыть");
            clickButton("Погон 1");
            clickButton("Погон 2");
            clickButton("Погон 3");
            clickButton("Погон - Коллекция");
        }

        if (type == BotType.RAID) {
            Thread.sleep(PAUSE_SHORT_MS);

            if (unifiedCounter.getCount() > 0) {
                clickButton("Клан");
                clickButton("Рейды");
            }

            clickButton("Атаковать босса");
            Thread.sleep(PAUSE_RAID_BOSS_MS);
            clickButton("Пропустить");
            clickButton("Закрыть");
        }

        minimizeActiveWindows();
        unifiedCounter.plusOne();
        CounterStorage.saveCounters(counters);
        System.out.println(Grammar.getWordEnd(unifiedCounter.getCount()));
        isGameGoingOn = LocalTime.now().isBefore(endTime) && (unifiedCounter.getCount() < totalBattles);
        if (isGameGoingOn) {
            countdown(FIVE_MINUTES_PAUSE_SECONDS - activeWindows.size() - lastSecondsCorrection);
        }
    }
}