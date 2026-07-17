package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.time.*;

import com.sun.jna.platform.win32.WinDef.HWND;

import static com.metrobot.Buttons.*;

/**
 * Режим "Арена": ежедневные бои перса. Самый первый режим работы программы! :-)
 * Вручную занимал у пользователей более 5 часов, раз в 5 минут требуя внимания.
 * Полное прохождение в полностью автоматическом режиме: порядка 4,5 часа = 50 боёв * 5 мин 10 сек = 260 минут.
 * В режиме "Арена" работает silent mode: окна разворачиваются перед серией кликов, затем сворачиваются обратно.
 * Повседневная работа пользователей в Windows прерывается раз в 5 минут всего на 10-12 секунд.
 * Счётчик боёв записывается в файл.
 * Большинство методов для всех классов-ботов унифицировано и вынесено в родительский BaseBot.
 */

public class ArenaBot extends BaseBot {
    public ArenaBot(List<HWND> windows,
                    LocalTime timeHHmm,
                    String botName,
                    boolean isPet, boolean isCloseAfterFinish) throws AWTException {

        super(windows);

        this.startTime = timeHHmm;
        this.botName = botName;
        this.isPet = isPet;
        this.isCloseAfterFinish = isCloseAfterFinish;
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return ARENA_BUTTONS;
    }

    @Override
    public void playGame() {
        try {
            startGame();

            //  === Бои на Арене ===
            for (int battle = (unifiedCounter.getBattleNumber() + 1); battle <= MAX_BATTLES_ARENA; battle++) {
                Instant battleStartTime = Instant.now();
                printBattleNumber(battle, MAX_BATTLES_ARENA);
                clickButtons("Арена 2", "Арена");
                clickButton("Атаковать");
                if (isPet) {
                    clickButton("Питомец");
                }

                clickButton("Пропустить");
                clickButton("Закрыть 1");
                clickButton("Закрыть 2");

                int battleDuration = fightEnd(battleStartTime);
                int secondsBeforeNextBattle = ATTACK_COOLDOWN_SEC - battleDuration;

                if (battle < MAX_BATTLES_ARENA) {
                    countdown(secondsBeforeNextBattle);
                }
            }

            endGame();
        } catch (Exception e) {
            handleExceptions(e);
        }
    }
}
