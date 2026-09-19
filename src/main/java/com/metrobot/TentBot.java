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

    private static final int FRIENDS_X = 43;
    private static final int FRIENDS_Y = 548; // запас ±5 px
    private static final int FRIENDS_WIDTH = 582;
    private static final int FRIENDS_HEIGHT = 22;

    @Override
    protected Map<String, Point> getButtonMap() {
        return TENT_BUTTONS;
    }


    @Override
    public void playGame() {
        try {
            startGame();

            for (HWND hWnd : activeWindows) {
                System.out.println("\n Боец " + (activeWindows.indexOf(hWnd) + 1));

                boolean[] vipFriends = findVipFriends(hWnd);

                System.out.println(Arrays.toString(vipFriends));
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

            System.out.printf("Друг %d: %d золотых пикселей → VIP: %s%n", friend + 1, goldPixels, vipFriends[friend]);
        }

        return vipFriends;
    }

    private BufferedImage captureArea(HWND hWnd) {
        RECT rect = getWindowRect(hWnd);
        Rectangle area = new Rectangle(
                rect.left + Buttons.xMoveRight + TentBot.FRIENDS_X,
                rect.top + Buttons.yMoveDown + TentBot.FRIENDS_Y,
                TentBot.FRIENDS_WIDTH,
                TentBot.FRIENDS_HEIGHT
        );

        return robot.createScreenCapture(area);
    }

    protected void getBullets() throws InterruptedException {
        clickButton("Друг");
        clickButtons(PAUSE_LONG_MS, "В гости", "Обыскать");
        clickButton("Назад");
    }
}