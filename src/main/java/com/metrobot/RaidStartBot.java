package com.metrobot;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.sun.jna.platform.win32.WinDef.HWND;

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

        {
            this.startTime = timeHHmm;
            this.botName = botName;
            this.windows = windows;
            this.closeAfterFinish = isCloseAfterFinish;
            this.bossName = bossName;
        }
    }

    private final List<HWND> windows;
    private final String bossName;

    @Override
    protected Map<String, Point> getButtonMap() {
        return CLAN_BUTTONS;
    }

    public void start() {
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

            System.out.println("\n=== Рейд запущен ==="); // стандартный endGame здесь не подходит
            Thread.sleep(PAUSE_LONG_MS);

            // Стартуем режим "Рейд" с теми окнами, которые передались в RaidStartBot
            ClanBot raid = new ClanBot(windows, LocalTime.now(), "Рейд", closeAfterFinish);
            raid.start();

        } catch (Exception e) {
            handleExceptions(e);
        }
    }
}
