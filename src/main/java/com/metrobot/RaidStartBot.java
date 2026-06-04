package com.metrobot;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.sun.jna.platform.win32.WinDef.HWND;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Objects;

import static com.metrobot.Buttons.*;

/**
 * Микро-режим для старта рейда в заданное время.
 * В заданное время Боец 1 заходит во вкладку Кланы -> Рейд, щёлкает на карте по выбранному босса и подтверждает рейд.
 * Однократно.
 * Метод announceInChat() оповещает игроков о старте рейда в чате игры.
 * После выполнения автоматически стартует режим Рейд для всех окон, выбранных в предыдущем диалоге.
 */
public class RaidStartBot extends BaseBot {
    public RaidStartBot(List<HWND> windows,
                        LocalTime timeHHmm,
                        String botName,
                        String bossName,
                        boolean isCloseAfterFinish) throws AWTException {

        super(List.of(windows.get(0))); // Запустить рейд может только первое, главное окно. Это логика игры.

        this.startTime = timeHHmm;
        this.botName = botName;
        this.windows = windows;
        this.closeAfterFinish = isCloseAfterFinish;
        this.bossName = bossName;
    }

    private final List<HWND> windows;
    private final String bossName;

    @Override
    protected Map<String, Point> getButtonMap() {
        return CLAN_BUTTONS;
    }

    @Override
    public void playGame() {
        try {
            startGame();

            boolean isBarracks = false;
            System.out.println("=== Запуск рейда ===");
            showActiveWindows();
            String consoleMessage = "босса " + bossName;
            System.out.println("\nВызываем " + consoleMessage);

            clickButton("Клан");
            clickButton("Рейды");
            Thread.sleep(PAUSE_SHORT_MS);

            if (Objects.equals(bossName, "Стигмат") ||
                    Objects.equals(bossName, "Горгон") ||
                    Objects.equals(bossName, "Тварь")
            ) {
                clickButton("Карта-ниже");
                clickButton("Карта-ниже");
                clickButton("Карта-левее");
            }

            clickButton(bossName);
            Thread.sleep(PAUSE_SHORT_MS);

            clickButton("В рейд");

            if (isBarracks) {
                barracksTurnOn();
            }

            announceInChat(isBarracks);

            System.out.printf("\n=== Вызов %s завершён ===", consoleMessage); // стандартный endGame здесь не подходит

            // Стартуем режим "Рейд" с теми окнами, которые передались в RaidStartBot
            ClanBot raid = new ClanBot(windows, LocalTime.now(), "Рейд", closeAfterFinish);
            raid.playGame();

        } catch (Exception e) {
            handleExceptions(e);
        }
    }

    protected void barracksTurnOn() throws Exception {
        clickButton("Строения");
        clickButton("Активировать");
        Thread.sleep(PAUSE_SHORT_MS);
    }

    protected void announceInChat(boolean isBarracks) throws Exception {
        final String MESSAGE_FOR_CLAN = "РЕЙД! Босс " + bossName + " запущен в автоматическом режиме, возможны ошибки";
        final String MESSAGE_BARRACKS = "Бараки работают!";

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_MINUS);
        robot.keyRelease(KeyEvent.VK_MINUS);
        robot.keyRelease(KeyEvent.VK_CONTROL);

        clickButton("Чат");

        robot.mouseMove(FREE_AREA_X, FREE_AREA_Y);
        robot.mouseWheel(WHEEL_AMOUNT);

        Thread.sleep(5000);
        clickButton("Чат - Клан");
        Thread.sleep(3000);

        clickButton("Чат - Строка");

        pasteText(MESSAGE_FOR_CLAN);
        System.out.printf("Печатаем сообщение \"%s\"\n", MESSAGE_FOR_CLAN);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

//        if (isBarracks) {
//            Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
//            pasteText(MESSAGE_BARRACKS);
//            robot.keyPress(KeyEvent.VK_ENTER);
//            robot.keyRelease(KeyEvent.VK_ENTER);
//        }

        robot.mouseMove(FREE_AREA_X, FREE_AREA_Y);
        robot.mouseWheel(-WHEEL_AMOUNT);

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_ADD);
        robot.keyRelease(KeyEvent.VK_ADD);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    protected void pasteText(String message) throws Exception {
        StringSelection selection = new StringSelection(message);

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        clipboard.setContents(selection, null);

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);

        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);

        Thread.sleep(PAUSE_SHORT_MS);
    }
}
