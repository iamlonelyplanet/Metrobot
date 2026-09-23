package com.metrobot;

import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.metrobot.Buttons.*;

public class TentBot extends BaseBot {
    public TentBot(List<HWND> windows,
                   LocalTime timeHHmm,
                   String botName,
                   boolean isCloseAfterFinish) throws AWTException {

        super(windows);

        this.startTime = timeHHmm;
        this.botName = botName;
        this.isCloseAfterFinish = isCloseAfterFinish;
    }

    public static final byte MAX_SCREENS = 25;
    private static final int FRIENDS_COUNT = 7;

    @Override
    protected Map<String, Point> getButtonMap() {
        return TENT_BUTTONS;
    }

    @Override
    public void playGame() {
        try {
            startGame();
            showActiveWindows();

            for (HWND hWnd : activeWindows) {
                int searchedTents = 0;
                clickButtons(hWnd, "Стрелка - начало");
                Thread.sleep(PAUSE_PET_MS);

                for (int sevenFriends = 0; sevenFriends < MAX_SCREENS; sevenFriends++) {
                    boolean[] vipFriends = findVipFriends(hWnd);
                    System.out.println(Arrays.toString(vipFriends));

                    for (int i = 0; i < vipFriends.length; i++) {
                        if (vipFriends[i]) {
                            System.out.printf("Порция 7 друзей №%d: обыскиваю друга %d\n", sevenFriends + 1, i + 1);
                            getBullets(hWnd, i);
                            searchedTents++;
                            if (searchedTents >= 10) {
                                break;
                            }
                        }
                    }
                    if (searchedTents == 10) {
                        break;
                    }

                    Thread.sleep(PAUSE_PET_MS);
                    clickButtons(hWnd, "Стрелка 7 вправо");
                    Thread.sleep(PAUSE_SHORT_MS);
                }

                Thread.sleep(PAUSE_PET_MS);
                clickButtons(hWnd, "Стрелка - начало");
                clickButtons(hWnd, "Назад");
            }

            endGame();
        } catch (Exception e) {
            handleExceptions(e);
        }
    }

    private boolean[] findVipFriends(HWND hWnd) {
        BufferedImage screenshot = captureArea(hWnd, ScreenshotArea.TENT);

        boolean[] vipFriends = new boolean[FRIENDS_COUNT];
        int friendWidth = screenshot.getWidth() / FRIENDS_COUNT;

        for (int friend = 0; friend < FRIENDS_COUNT; friend++) {

            int startX = friend * friendWidth;
            int endX = (friend == FRIENDS_COUNT - 1)
                    ? screenshot.getWidth()
                    : (friend + 1) * friendWidth;

            int goldPixels = countPixelsByColor(
                    screenshot,
                    startX,
                    endX,
                    ScreenshotArea.TENT.colorCriteria()
            );

            vipFriends[friend] = goldPixels >= ScreenshotArea.TENT.colorCriteria().minPixels();

            System.out.printf("Друг %d: %d золотых пикселей: %s%n", friend + 1, goldPixels, vipFriends[friend]);
        }

        return vipFriends;
    }

    protected void getBullets(HWND hWnd, int friendNumber) throws InterruptedException {
        int x = 85 + friendNumber * 85;
        Point friendPoint = new Point(x, 555);
        Point housePoint = new Point(x, 525);

        clickAtWindow(hWnd, friendPoint);
        Thread.sleep(PAUSE_PET_MS);

        clickAtWindow(hWnd, housePoint);
        Thread.sleep(PAUSE_SHORT_MS);

        clickButtons(hWnd, "Обыскать");
        Thread.sleep(PAUSE_SHORT_MS);
    }
}