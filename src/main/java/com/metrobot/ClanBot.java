package com.metrobot;

import java.awt.*;
import java.time.Instant;
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
 * TODO: Завершение боёв по таймеру. Исправить закрытие и открытие автобоя, снизив y для нижних окон.
 */

public class ClanBot extends BaseBot {
    public ClanBot(List<HWND> windows,
                   LocalTime timeHHmm,
                   String botName,
                   boolean closeAfterFinish) throws AWTException {

        super(windows);

        this.startTime = timeHHmm;
        this.botName = botName;
        this.windows = windows;
        this.closeAfterFinish = closeAfterFinish;
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

    public enum BotType {RAID, CW}

    @Override
    public void playGame() {
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
                    unCheckAutoFight();
                    clickButtons("Клан", "Война", "Обновить");
                    clickButton("Рейды");
                    lastAttackMillis = System.currentTimeMillis();
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
        Instant startTime = Instant.now();
        if (unifiedCounter.getCount() == 0) {
            unCheckAutoFight();
        }

        if (type == BotType.CW) {
            clickButtons("Клан", "Война");
            lastAttackMillis = System.currentTimeMillis();
            clickButton("Атаковать врага");
            Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
            clickButton("Пропустить");
            clickButton("Закрыть");
            clickButtons("Погон 1", "Погон 2", "Погон 3", "Погон - Коллекция");
//            clickButton("Погон - Коллекция");
        }

        if (type == BotType.RAID) {
            if (unifiedCounter.getCount() > 0) {
                clickButtons("Клан", "Рейды");
                lastAttackMillis = System.currentTimeMillis();
            }

            clickButton("Атаковать босса");
            Thread.sleep(PAUSE_RAID_BOSS_MS);

            boolean isGrenadeModeOn = false;
            if (isGrenadeModeOn) {
                Thread.sleep(PAUSE_SHORT_MS);
                clickButton("Граната красная");
                Thread.sleep(5000);
            }
            clickButton("Пропустить");
            clickButton("Закрыть - Рейд");
        }

        clickButton("Клан - Выход"); // Выход из игрового меню "Клан" радикально снижает загрузку CPU

        fightEnd(startTime);

        LocalTime nextBattleTime = LocalTime.now().plusSeconds(ATTACK_COOLDOWN_SEC);
        isGameGoingOn = nextBattleTime.isBefore(endTime) && (unifiedCounter.getCount() < totalBattles);

        if (isGameGoingOn) {
            if (lastAttackMillis > 0) {
                long now = System.currentTimeMillis();
                long elapsedSeconds = (now - lastAttackMillis) / 1000;
                long secondsToWait = ATTACK_COOLDOWN_SEC - elapsedSeconds;

                if (secondsToWait > 0) {
                    countdown((int) secondsToWait);
                }
            }
        }
    }

    // Выход из режима "Автобой" (при VIP) перед ClanBot. Не сработает, если нижние окна пересекаются с верхними.
    private void unCheckAutoFight() throws InterruptedException {
        clickButton("Автобой");
        clickButton("Арена - закрыть");
    }
}