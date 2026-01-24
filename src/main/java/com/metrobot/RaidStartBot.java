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
 * После выполнения режима автоматически стартует режим Рейд во всех окнах, выбранных в предыдущем диалоге.
 */
public class RaidStartBot extends BaseBot {

    public RaidStartBot(List<HWND> windows,
                        LocalTime timeHHmm,
                        String botName,
                        boolean isCloseAfterFinish) throws AWTException {

        super(List.of(windows.get(0))); // запустить Рейд может только первое, главное окно. Так надо по логике игры.

        {
            this.startTime = timeHHmm;
            this.botName = botName;
            this.windows = windows;
            this.closeAfterFinish = isCloseAfterFinish;
        }
    }
    private final List<HWND> windows;

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

            System.out.println("=== Рейд запущен ==="); // стандартный endGame здесь не подходит
            Thread.sleep(PAUSE_LONG_MS);

            // Стартуем режим "Рейд" с теми окнами, которые передались в RaidStartBot
            ClanBot raid = new ClanBot(windows, LocalTime.now(), "Рейд", closeAfterFinish);
            raid.start();


        } catch (Exception e) {
            handleExceptions(e);
        }
    }
}
