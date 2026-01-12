package com.metrobot;

import java.time.LocalTime;
import java.util.*;
import java.util.List;

import com.sun.jna.platform.win32.WinDef.HWND;

import javax.swing.text.Utilities;

/**
 * Главный класс. Спрашивает в GUI режим игры, активные окна, время старта каждого режима. Обнуляет счётчики
 * раз в сутки после 03:00 Мск, так нужно по логике игры.
 * Здесь и в следующих классах прошу ориентироваться на комментарии перед методами.
 * TODO: совместить 3 основных класса (боты Арена, КВ и Рейд) в единый. ООП же!
 * TODO: унифицировать switch/case! В отдельный класс?
 */

public class Main {
    public static void main(String[] args) {
        try {
//            String botName;
//            LocalTime startTime;

            // === Обнуляем файл счётчиков каждый день при первом запуске программы после 03:00 по Мск, так надо. ===
            ConfigManager.autoResetCounters();

            // === Загружаем конфиг из файла при наличии ===
            Map<String, String> config = ConfigManager.loadConfig();

            Optional<BotStartConfig> result =
                    BotStartWizard.askUser(config);

            if (result.isEmpty()) {
                System.out.println("Запуск отменён пользователем.");
                return;
            }

            BotStartConfig cfg = result.get();
            ConfigManager.saveConfig(
                    cfg.getMode(),
                    cfg.getActiveWindows(),
                    // тут пока можешь оставить старую логику времён
            );

//            BotFactory.start(cfg);

//            System.out.println("=== Проверка конфигурации ===");
//            System.out.println("mode = " + cfg.getMode());
//            System.out.println("botName = " + cfg.getBotName());
//            System.out.println("startTime = " + cfg.getStartTime());
//            System.out.println("usePet = " + cfg.isUsePet());
//            System.out.println("windows = " + cfg.getActiveWindows().size());
//            System.out.println("============================");


//            // === Запрашиваем режим игры в режиме GUI ===
//            Utilites.ModeSelection selection = Utilites.askModeSelection();

//            int mode = Utilites.askMode();
//            boolean usePet = Utilites.usePet;
//
//            // === Разворачиваем окна игры по заголовку. Каждое окно = перс/боец ===
//            Utilites.restoreAllGameWindows();
//
//            // === Получаем координаты каждого окна среди развёрнутых ===
//            List<HWND> foundWindows = Utilites.findGameWindows();
//
//            // === Запрашиваем в режиме GUI активные окна из числа найденных, с ними будет работать программа ===
//            List<HWND> activeWindows = Utilites.askActiveWindows(foundWindows, config.get("activeWindows"));
//
            // === Читаем времена стартов из конфига (если есть). Не трогать, пока хоть как-то работает ===
            LocalTime arenaDefault = Utilites.parseTime(config.get("arena_start"));
            LocalTime kvDefault = Utilites.parseTime(config.get("kv_start"));
            LocalTime raidDefault = Utilites.parseTime(config.get("raid_start"));
            LocalTime tunnelDefault = Utilites.parseTime(config.get("tunnel_start"));
            LocalTime ratDefault = Utilites.parseTime(config.get("rat_start"));

            // === Готовим переменные времени для записи обратно в конфиг. Не трогать, пока хоть как-то работает ===
            LocalTime arenaStart = arenaDefault;
            LocalTime kvStart = kvDefault;
            LocalTime raidStart = raidDefault;
            LocalTime tunnelStart = tunnelDefault;
            LocalTime ratStart = ratDefault;

            // === Запуск выбранного режима игры ===
            switch (mode) {
                case 1 -> {
                    botName = "КВ";
                    startTime = Utilites.askStartTime(botName, kvDefault);
                    kvStart = startTime;
                    ConfigManager.saveConfig(mode, activeWindows, arenaStart, kvStart, raidStart, tunnelStart, ratStart);
                    ClanBot clanBot = new ClanBot(activeWindows, startTime, botName);
                    clanBot.start();
                }
                case 2 -> {
                    botName = "Рейд";
                    startTime = Utilites.askStartTime(botName, raidDefault);
                    raidStart = startTime;
                    ConfigManager.saveConfig(mode, activeWindows, arenaStart, kvStart, raidStart, tunnelStart, ratStart);
                    ClanBot clanBot = new ClanBot(activeWindows, startTime, botName);
                    clanBot.start();
                }
                case 3 -> {
                    botName = "Арена";
                    startTime = Utilites.askStartTime(botName, arenaDefault);
                    arenaStart = startTime;
                    ConfigManager.saveConfig(mode, activeWindows, arenaStart, kvStart, raidStart, tunnelStart, ratStart);
                    ArenaBot arenaBot = new ArenaBot(activeWindows, startTime, botName, usePet);
                    arenaBot.start();
                }
                case 4 -> {
                    botName = "Туннель";
                    startTime = Utilites.askStartTime(botName, tunnelDefault);
                    tunnelStart = startTime;
                    ConfigManager.saveConfig(mode, activeWindows, arenaStart, kvStart, raidStart, tunnelStart, ratStart);
                    TunnelBot tunnelBot = new TunnelBot(activeWindows, startTime, botName, usePet);
                    tunnelBot.start();
                }
                case 5 -> {
                    botName = "Крысы";
                    startTime = Utilites.askStartTime(botName, ratDefault);
                    ratStart = startTime;
                    ConfigManager.saveConfig(mode, activeWindows, arenaStart, kvStart, raidStart, tunnelStart, ratStart);
                    RatBot ratBot = new RatBot(activeWindows, startTime, botName, usePet);
                    ratBot.start();
                }
                default -> System.out.println("Неизвестный режим. Завершаю.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}