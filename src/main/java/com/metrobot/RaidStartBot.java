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
 * После выполнения во всех окнах, выбранных в предыдущем диалоге, автоматически стартует режим Рейд.
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

            System.out.println("=== Запуск рейда ===");
            showActiveWindows();
            System.out.println("\nВызываем босса " + bossName);

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

            announceInChat();

            System.out.println("\n=== Выбора босса для рейда завершён ==="); // стандартный endGame здесь не подходит
            Thread.sleep(PAUSE_SHORT_MS);

            // Стартуем режим "Рейд" с теми окнами, которые передались в RaidStartBot
            ClanBot raid = new ClanBot(windows, LocalTime.now(), "Рейд", closeAfterFinish);
            raid.playGame();

        } catch (Exception e) {
            handleExceptions(e);
        }
    }

    protected void announceInChat() throws Exception {
        final String MESSAGE_FOR_CLAN = "РЕЙД! " + bossName + ". Рейд запущен в автоматическом режиме, возможны ошибки";
        final String MESSAGE_FOR_CLAN_2 = "1";

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_MINUS);
        robot.keyRelease(KeyEvent.VK_MINUS);
        robot.keyRelease(KeyEvent.VK_CONTROL);

        clickButton("Чат");

        robot.mouseMove(FREE_AREA_X, FREE_AREA_Y);
        robot.mouseWheel(WHEEL_AMOUNT);

        Thread.sleep(5000);
        clickButton("Чат - Клан");
        Thread.sleep(5000);

        System.out.printf("Печатаем сообщение \"%s\"\n", MESSAGE_FOR_CLAN);
        pasteText(MESSAGE_FOR_CLAN);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

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
