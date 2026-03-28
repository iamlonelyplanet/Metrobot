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
 * Вручную занимал у пользователей порядка 2 (КВ) и 1 часа (рейд), раз в 5 минут требуя внимания, притом сильно:
 * коллектив же.
 * <p>
 * Полное прохождение: то же время, полностью автоматически.
 * В этом режиме работает silent mode: окна разворачиваются перед серией кликов, затем сворачиваются обратно.
 * Повседневная работа пользователя в Windows прерывается всего на 10-12 секунд раз в 5 минут.
 * Счётчик боёв записывается в файл.
 *
 * TODO: проверить предпоследние, 23/11 бои. Переделать закрытие автобоя
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
    private boolean isGameGoingOn;
    private LocalTime endTime;
    private long lastAttackMillis = -1;
    boolean g = true;

    public void start() {
        try {
            startGame();
            int hoursToFinish;
            if (Objects.equals(botName, "Рейд")) {
                hoursToFinish = 1;
                totalBattles = MAX_BATTLES_RAID;
            } else {
                hoursToFinish = 2;
                totalBattles = MAX_BATTLES_CW;
            }

            endTime = startTime.plusHours(hoursToFinish);
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
        /* Выход из режима "Автобой" (при VIP) перед клановыми движухами. Не сработает, если нижние окна
        пересекаются с верхними. Переделать?
         */
//        if (unifiedCounter.getCount() == 0) {
//            clickButton("Убрать автобой");
//            clickButton("Арена - закрыть");
//        }

        if (type == BotType.CW) {
            clickButton("Клан");
            lastAttackMillis = System.currentTimeMillis();
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
            // поковыряться здесь в таймингах для старта клана
            if (unifiedCounter.getCount() > 0) {
                clickButton("Клан");
                lastAttackMillis = System.currentTimeMillis();
                clickButton("Рейды");
            }

            clickButton("Атаковать босса");
            lastAttackMillis = System.currentTimeMillis();
            Thread.sleep(PAUSE_RAID_BOSS_MS);
//            if (g) {
//
//            }
            clickButton("Пропустить");
            clickButton("Закрыть");
        }

        minimizeActiveWindows();
        unifiedCounter.plusOne();
        CounterStorage.saveCounters(counters);
        System.out.println(Grammar.getWordEnd(unifiedCounter.getCount()));
        isGameGoingOn = LocalTime.now().isBefore(endTime) && (unifiedCounter.getCount() < totalBattles);

        if (isGameGoingOn) {
            if (lastAttackMillis > 0) {
                long now = System.currentTimeMillis();
                long elapsedSeconds = (now - lastAttackMillis) / 1000;
                long secondsToWait = ATTACK_COOLDOWN_SECONDS - elapsedSeconds;

                if (secondsToWait > 0) {
                    countdown((int) secondsToWait);
                }
            }
        }
    }
}