package com.metrobot;

import java.awt.*;

public class BotFactory {
    // Класс для экспериментов с паттерном Factory
    public static void start(BotStartConfig cfg) throws AWTException {
        // === Запуск выбранного режима игры ===
        switch (cfg.getMode()) {
            case 1 -> { // КВ, не мёржить с case 2
                ClanBot clanBot = new ClanBot(
                        cfg.getActiveWindows(),
                        cfg.getStartTime(),
                        cfg.getBotName()
                );
                clanBot.start();
            }
            case 2 -> { // Рейд, не мёржить с case 1
                ClanBot clanBot = new ClanBot(
                        cfg.getActiveWindows(),
                        cfg.getStartTime(),
                        cfg.getBotName()
                );
                clanBot.start();
            }
            case 3 -> {
                ArenaBot arenaBot = new ArenaBot(
                        cfg.getActiveWindows(),
                        cfg.getStartTime(),
                        cfg.getBotName(),
                        cfg.isUsePet()
                );
                arenaBot.start();
            }
            case 4 -> {
                TunnelBot tunnelBot = new TunnelBot(
                        cfg.getActiveWindows(),
                        cfg.getStartTime(),
                        cfg.getBotName(),
                        cfg.isUsePet()
                );
                tunnelBot.start();
            }
            case 5 -> {
                RatBot ratBot = new RatBot(
                        cfg.getActiveWindows(),
                        cfg.getStartTime(),
                        cfg.getBotName(),
                        cfg.isUsePet()
                );
                ratBot.start();
            }

            case 6 -> {
                RaidStart RaidStart = new RaidStart(
                        cfg.getActiveWindows(),
                        cfg.getStartTime(),
                        cfg.getBotName(),
                        cfg.isUsePet()
                );
                RaidStart.start();
            }
            default -> System.out.println("Неизвестный режим. Завершаю.");
        }
    }

}
