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

    private static final int X = 43;
    private static final int Y = 548;
    private static final int WIDTH = 582;
    private static final int HEIGHT = 22;

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
                Thread.sleep(200);

                for (int sevenFriends = 0; sevenFriends < MAX_SCREENS; sevenFriends++) {
                    boolean[] vipFriends = findVipFriends(hWnd);
                    System.out.println(Arrays.toString(vipFriends));

                    for (int i = 0; i < vipFriends.length; i++) {
                        if (vipFriends[i]) {
                            System.out.printf("Экран %d: обыскиваю друга %d", i + 1, sevenFriends + 1);
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

                clickButtons(hWnd, "Стрелка - начало");
            }

            endGame();
        } catch (Exception e) {
            handleExceptions(e);
        }
    }

    private boolean[] findVipFriends(HWND hWnd) {
        BufferedImage screenshot = captureArea(hWnd);
        boolean[] vipFriends = new boolean[FRIENDS_COUNT];
        int friendWidth = screenshot.getWidth() / FRIENDS_COUNT;

        for (int friend = 0; friend < FRIENDS_COUNT; friend++) {

            int startX = friend * friendWidth;
            int endX = (friend == FRIENDS_COUNT - 1)
                    ? screenshot.getWidth()
                    : (friend + 1) * friendWidth;

            int goldPixels = 0;

            for (int y = 0; y < screenshot.getHeight(); y++) {
                for (int x = startX; x < endX; x++) {

                    Color color = new Color(screenshot.getRGB(x, y));

                    int r = color.getRed();
                    int g = color.getGreen();
                    int b = color.getBlue();

                    if (r > 200 && g > 150 && b < 160 && r - g > 20 && g - b > 30) {
                        goldPixels++;
                    }
                }
            }

            vipFriends[friend] = goldPixels >= 10;

            System.out.printf("Друг %d: %d золотых пикселей: %s%n", friend + 1, goldPixels, vipFriends[friend]);
        }

        return vipFriends;
    }

    private BufferedImage captureArea(HWND hWnd) {
        RECT rect = getWindowRect(hWnd);
        Rectangle area = new Rectangle(
                rect.left + Buttons.xMoveRight + X,
                rect.top + Buttons.yMoveDown + Y,
                WIDTH,
                HEIGHT
        );

        return robot.createScreenCapture(area);
    }

    protected void getBullets(HWND hWnd, int friendNumber) throws InterruptedException {
        int x = 85 + friendNumber * 85;
        Point friendPoint = new Point(x, 555);
        Point housePoint = new Point(x,525);

        clickAtWindow(hWnd, friendPoint);
        Thread.sleep(PAUSE_PET_MS);

        clickAtWindow(hWnd, housePoint);
        Thread.sleep(PAUSE_SHORT_MS);

        clickButtons(hWnd,"Обыскать");
        Thread.sleep(PAUSE_SHORT_MS);
    }
}