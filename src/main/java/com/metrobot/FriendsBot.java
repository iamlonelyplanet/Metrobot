package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.time.*;

import com.sun.jna.platform.win32.WinDef.HWND;

import static com.metrobot.Buttons.*;

/**
 * Режим "Друзья": ежедневные бои перса. Очень близок к режиму "Арена", делят единый счётчик на двоих.
 * Вручную занимал у пользователей более 5 часов, раз в 5 минут требуя внимания.
 * Полное прохождение в полностью автоматическом режиме: порядка 4,5 часа = 50 боёв * 5 мин 10 сек = 260 минут.
 * В режиме "Друзья" работает silent mode: окна разворачиваются перед серией кликов, затем сворачиваются обратно.
 * Повседневная работа пользователей в Windows прерывается раз в 5 минут всего на 10-14 секунд.
 * Счётчик боёв записывается в файл.
 * Большинство методов для всех классов-ботов унифицировано и вынесено в родительский BaseBot.
 * TODO: реализовать одновременную работу нескольких окон, без переключения между ними при первых кликах.
 */

public class FriendsBot extends BaseBot {
    public FriendsBot(List<HWND> windows,
                      LocalTime timeHHmm,
                      String botName,
                      boolean isPet, boolean isCloseAfterFinish) throws AWTException {

        super(windows);

        this.startTime = timeHHmm;
        this.botName = botName;
        this.isCloseAfterFinish = isCloseAfterFinish;
        this.isPet = isPet;
    }

    public static final byte MAX_BATTLES_ARENA = 50;

    @Override
    protected String getCounterName() {
        return "Арена";
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return ARENA_BUTTONS;
    }

    @Override
    public void playGame() {
        try {
            startGame();

            //  === Бои с друзьями ===
            boolean isFirstBattle = false;

            for (int battle = unifiedCounter.getBattleNumber() + 1; battle <= MAX_BATTLES_ARENA; battle++) {
                Instant battleStartTime = Instant.now();
                printBattleNumber(battle, MAX_BATTLES_ARENA);
                clickButtons(600, "Друг", "Атаковать друга");
                Thread.sleep(1800);
                clickButton("Атаковать");

                if (isPet) {
                    clickButton("Питомец");
                }

                clickButton("Стрелка вправо");
                clickButton("Пропустить");
                if (isFirstBattle) {
                    clickButtons(200,"Похвастаться - снять", "Похвастаться - закрыть");
                    isFirstBattle = false;
                } else {
                    clickButton("Закрыть 1");
                    clickButton("Закрыть - поражение");
                }

                int battleDuration = fightEnd(battleStartTime);
                int secondsBeforeNextBattle = ATTACK_COOLDOWN_SEC - battleDuration + 1;
                boolean isGameGoingOn = battle < MAX_BATTLES_ARENA;
                if (isGameGoingOn) {
                    countdown(secondsBeforeNextBattle);
                }
            }

            endGame();
        } catch (Exception e) {
            handleExceptions(e);
        }
    }
}

