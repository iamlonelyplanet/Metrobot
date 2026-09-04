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
        this.isCloseAfterFinish = isCloseAfterFinish;
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
            uncheckAutoFight();
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

            if (isBarracks) {
                barracksTurnOn();
            }

            announceInChat(isBarracks);

            System.out.printf("\n=== Запуск рейда против босса %s завершён ===", bossName); // endGame не подходит

            // Стартуем режим "Рейд" с теми окнами, которые передались в RaidStartBot
            ClanBot raid = new ClanBot(windows, LocalTime.now(), "Рейд", isCloseAfterFinish);
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
        final String MESSAGE_FOR_CLAN = "РЕЙД! Босс " + bossName + " запущен программой. Проверьте";
        final String MESSAGE_BARRACKS = "Бараки работают!";

        pressKeyCombination(KeyEvent.VK_MINUS);

        clickButton("Чат");
        robot.mouseMove(FREE_AREA_X, FREE_AREA_Y);
        robot.mouseWheel(WHEEL_AMOUNT);

        Thread.sleep(1500);
        clickButton("Чат - Клан");
        Thread.sleep(1500);
        clickButton("Чат - Строка");

        pasteText(MESSAGE_FOR_CLAN);
        pressKeyCombination(KeyEvent.VK_ENTER);

        if (isBarracks) {
            Thread.sleep(PAUSE_SHORT_TUNNELS_MS);
            pasteText(MESSAGE_BARRACKS);
            pressKey(KeyEvent.VK_ENTER);
        }

        Thread.sleep(1000);
        robot.mouseMove(FREE_AREA_X, FREE_AREA_Y);
        robot.mouseWheel(-WHEEL_AMOUNT);

        pressKeyCombination(KeyEvent.VK_ADD);
    }

    protected void pasteText(String message) throws Exception {
        System.out.printf("Печатаем сообщение \"%s\"\n", message);

        StringSelection selection = new StringSelection(message);

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        clipboard.setContents(selection, null);

        pressKeyCombination(KeyEvent.VK_V);
        Thread.sleep(PAUSE_SHORT_MS);
    }

    protected void pressKey(int key) {
        robot.keyPress(key);
        robot.keyRelease(key);
    }

    protected void pressKeyCombination(int key2) {
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(key2);
        robot.keyRelease(key2);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }
}
