package com.metrobot;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.time.*;

import com.sun.jna.platform.win32.WinDef.RECT;
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
            for (int battle = unifiedCounter.getBattleNumber() + 1; battle <= MAX_BATTLES_ARENA; battle++) {
                Instant battleStartTime = Instant.now();
                printBattleNumber(battle, MAX_BATTLES_ARENA);
                clickButtons(600, "Друг", "Атаковать друга");
                Thread.sleep(1600); // 1800
                clickButton("Атаковать");

                if (isPet) {
                    clickButton("Питомец");
                }

                clickButton("Стрелка вправо");
                clickButton("Пропустить");
                Thread.sleep(500);

                for (HWND hWnd : activeWindows) {
                    if (isBoxChecked(hWnd)) {
                        clickButtons(hWnd, 200, "Похвастаться - снять",
                                "Похвастаться - закрыть");
                    } else {
                        clickButton(hWnd, "Закрыть 1");
                        clickButton(hWnd, "Закрыть - поражение");
                        minimizeActiveWindow(hWnd, activeWindows.size() + 1);
                    }
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

    private boolean isBoxChecked(HWND hWnd) {
        BufferedImage screenshot = captureArea(hWnd, ScreenshotArea.SEVEN_FRIENDS);

        int coloredPixels = countPixelsByColor(
                screenshot,
                0,
                screenshot.getWidth(),
                ScreenshotArea.SEVEN_FRIENDS.colorCriteria()
        );

        return coloredPixels >= 20;
    }
}

