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
            isGameGoingOn = LocalTime.now().isBefore(endTime) && (unifiedCounter.getBattleNumber() < totalBattles);

            if (Objects.equals(botName, "Рейд")) {
                // Подготовительные клики (однократно, перед первым боем рейда)
                if (unifiedCounter.getBattleNumber() == 0) {
                    showActiveWindows();
                    unCheckAutoFight();
                    clickButtons("Клан", "Война", "Обновить");
                    clickButton("Рейды");
                    Instant battleStartTime = Instant.now();
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
        printBattleNumber((unifiedCounter.getBattleNumber() + 1), totalBattles);
        Instant battleStartTime = Instant.now();
        if (unifiedCounter.getBattleNumber() == 0) {
            unCheckAutoFight();
        }

        if (type == BotType.CW) {
            clickButtons("Клан", "Война");
            battleStartTime = Instant.now();
            clickButton("Атаковать врага");
            Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
            clickButton("Пропустить");
            clickButton("Закрыть");
            clickButtons("Погон 1", "Погон 2", "Погон 3");
            clickButton("Погон - Коллекция");
        }

        if (type == BotType.RAID) {
            if (unifiedCounter.getBattleNumber() > 0) {
                clickButtons("Клан", "Рейды");
                battleStartTime = Instant.now();
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

        int battleDuration = fightEnd(battleStartTime);

        LocalTime nextBattleTime = LocalTime.now().plusSeconds(ATTACK_COOLDOWN_SEC);
        isGameGoingOn = nextBattleTime.isBefore(endTime) && (unifiedCounter.getBattleNumber() < totalBattles);

        if (isGameGoingOn) {
            int secondsBeforeNextBattle = ATTACK_COOLDOWN_SEC - battleDuration;
            countdown(secondsBeforeNextBattle);
        }
    }
}