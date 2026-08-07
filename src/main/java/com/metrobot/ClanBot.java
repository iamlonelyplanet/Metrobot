package com.metrobot;

import java.awt.*;
import java.time.Duration;
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
 * Вручную занимал у пользователей порядка 2 (КВ) и 1 часа (рейд), раз в 5 минут требуя внимания, притом сильно:
 * коллектив же.
 * <p>
 * Полное прохождение: то же время, полностью автоматически.
 * В этом режиме работает silent mode: окна разворачиваются перед серией кликов, затем сворачиваются обратно.
 * Повседневная работа пользователя в Windows прерывается раз в 5 минут всего на 10-25 секунд.
 * Счётчик боёв записывается в файл.
 * TODO: Завершение рейда, когда босс прибит. После 6-8 удара?
 */

public class ClanBot extends BaseBot {
    public ClanBot(List<HWND> windows,
                   LocalTime timeHHmm,
                   String botName,
                   boolean isCloseAfterFinish) throws AWTException {

        super(windows);

        this.startTime = timeHHmm;
        this.botName = botName;
        this.windows = windows;
        this.isCloseAfterFinish = isCloseAfterFinish;
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return CLAN_BUTTONS;
    }

    protected final List<HWND> windows;
    private int totalBattles;
    private boolean isGameGoingOn;
    private Instant endInstant;

    public enum BotType {RAID, CW}

    public static final int PAUSE_RAID_BOSS_MS = 12_800;

    @Override
    public void playGame() {
        try {
            startGame();

            Duration clanActivityDuration;
            if (Objects.equals(botName, "Рейд")) {
                clanActivityDuration = Duration.ofMinutes(60);
                totalBattles = MAX_BATTLES_RAID;
            } else {
                clanActivityDuration = Duration.ofMinutes(120);
                totalBattles = MAX_BATTLES_CW;
            }

            endInstant = Instant.now().plus(clanActivityDuration);
            isGameGoingOn = Instant.now().isBefore(endInstant) && (unifiedCounter.getBattleNumber() < totalBattles);

            if (Objects.equals(botName, "Рейд")) {
                // Подготовительные клики (однократно, перед первым боем рейда)
                if (unifiedCounter.getBattleNumber() == 0) {
                    showActiveWindows();
                    unCheckAutoFight();
                    clickButtons("Клан", "Война", "Обновить");
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
        printBattleNumber(unifiedCounter.getBattleNumber() + 1, totalBattles);
        Instant battleStartTime = Instant.now();

        if (unifiedCounter.getBattleNumber() == 0 && type == BotType.CW) {
            unCheckAutoFight();
        }

        if (type == BotType.CW) {
            clickButtons("Клан", "Война", "Атаковать врага");
            Thread.sleep(800);
            clickButton("Пропустить");
            clickButton("Закрыть");
            clickButtons("Погон 1", "Погон 2", "Погон 3", "Погон - Коллекция");
        }

        if (type == BotType.RAID) {
            clickButtons("Клан", "Рейды");
            Thread.sleep(PAUSE_SHORT_TUNNELS_MS);// Пересмотреть на предмет "Атаковать босса" сюда (21.07)
            clickButton("Атаковать босса");
            Thread.sleep(PAUSE_RAID_BOSS_MS);

            boolean isGrenadeModeOn = false;
            if (isGrenadeModeOn) {
                Thread.sleep(PAUSE_SHORT_MS);
                clickButton("Граната красная");
                Thread.sleep(22000);
                clickButton("Граната красная");
                Thread.sleep(PAUSE_SHORT_MS);
            }

            clickButton("Пропустить");
            clickButton("Закрыть - Рейд");
        }

        // Общая часть для обоих режимов
        clickButton("Клан - Выход"); // Выход из игрового меню "Клан" радикально снижает загрузку CPU

        int battleDuration = fightEnd(battleStartTime);
        int secondsBeforeNextBattle = ATTACK_COOLDOWN_SEC - battleDuration;
        Instant nextBattleTime = Instant.now().plusSeconds(secondsBeforeNextBattle);

        isGameGoingOn = nextBattleTime.isBefore(endInstant)
                && (unifiedCounter.getBattleNumber() < totalBattles);

        if (isGameGoingOn) {
            countdown(secondsBeforeNextBattle);
        }
    }
}