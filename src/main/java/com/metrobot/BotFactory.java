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
                        cfg.getBotName(),
                        cfg.isCloseAfterFinish());
                clanBot.playGame();
            }
            case 2 -> { // Рейд, не мёржить с case 1
                ClanBot clanBot = new ClanBot(
                        cfg.getActiveWindows(),
                        cfg.getStartTime(),
                        cfg.getBotName(),
                        cfg.isCloseAfterFinish()
                );
                clanBot.playGame();
            }
            case 3 -> {
                ArenaBot arenaBot = new ArenaBot(
                        cfg.getActiveWindows(),
                        cfg.getStartTime(),
                        cfg.getBotName(),
                        cfg.isPet(),
                        cfg.isCloseAfterFinish()
                );
                arenaBot.playGame();
            }
            case 4 -> {
                TunnelBot tunnelBot = new TunnelBot(
                        cfg.getActiveWindows(),
                        cfg.getStartTime(),
                        cfg.getBotName(),
                        cfg.isPet(),
                        cfg.isCloseAfterFinish()
                );
                tunnelBot.playGame();
            }
            case 5 -> {
                RatBot ratBot = new RatBot(
                        cfg.getActiveWindows(),
                        cfg.getStartTime(),
                        cfg.getBotName(),
                        cfg.isPet(),
                        cfg.isCloseAfterFinish()
                );
                ratBot.playGame();
            }

            case 6 -> {
                RaidStartBot RaidStartBot = new RaidStartBot(
                        cfg.getActiveWindows(),
                        cfg.getStartTime(),
                        cfg.getBotName(),
                        cfg.getBoss(),
                        cfg.isCloseAfterFinish()
                );
                RaidStartBot.playGame();
            }
            default -> System.out.println("Неизвестный режим. Завершаю.");
        }
    }

}
