package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.sun.jna.platform.win32.WinDef.HWND;

import static com.metrobot.Buttons.*;

/**
 * Микро-режим для старта рейда в заданное время.
 * В заданное время Боец 1 заходит во вкладку Кланы, затем в Рейд, щёлкает на вызов босса и подтверждает рейд.
 * Однократно.
 * Надо убрать usePet.
 */
public class RaidStart extends BaseBot {

    public RaidStart(List<HWND> windows,
                     LocalTime timeHHmm,
                     String botName) throws AWTException {

        super(windows);

        {
            this.startTime = timeHHmm;
            this.botName = botName;
        }
    }

    @Override
    protected Map<String, Point> getButtonMap() {
        return CLAN_BUTTONS;
    }

    public void start() {
        try {
            startGame();
            showActiveWindows();

            System.out.println("=== Запуск рейда ===");

            clickButton("Клан");
            Thread.sleep(PAUSE_SHORT_MS);

            clickButton("Рейды");
            Thread.sleep(PAUSE_SHORT_MS);

            clickButton("Вичуха");
            Thread.sleep(PAUSE_SHORT_MS);

            clickButton("В рейд");

            System.out.println("=== Рейд запущен ==="); // стандартный endGame здесь не подходит.

        } catch (Exception e) {
            handleExceptions(e);
        }
    }
}
