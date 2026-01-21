package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.sun.jna.platform.win32.WinDef.HWND;

import static com.metrobot.Buttons.*;

/**
 * Микро-режим: RaidStart
 * В заданное время Боец 1 заходит в клановый рейд и стартует его.
 * Без повторений.
 */
public class RaidStart extends BaseBot {

    public RaidStart(List<HWND> windows,
                     LocalTime timeHHmm,
                     String botName, boolean usePet) throws AWTException {

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
            startGame();          // ожидание startTime + фокус окон
            showActiveWindows();  // на всякий случай

            System.out.println("=== Запуск рейда ===");

            // Только Боец 1 — предполагаем, что окно уже выбрано BaseBot'ом
            clickButton("Клан");
            Thread.sleep(PAUSE_SHORT_MS);

            clickButton("Рейды");
            Thread.sleep(PAUSE_SHORT_MS);

            clickButton("Вичуха");
            Thread.sleep(PAUSE_SHORT_MS);

            clickButton("В рейд");

            System.out.println("=== Рейд запущен ===");

        } catch (Exception e) {
            handleExceptions(e);
        }
    }
}
