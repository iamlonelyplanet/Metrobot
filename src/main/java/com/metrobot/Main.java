package com.metrobot;

import java.awt.*;
import java.io.*;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

import com.sun.jna.platform.win32.WinDef.HWND;

import javax.swing.*;

/**
 * Главный класс. Спрашивает в GUI/консоли: режим игры, активные окна, время старта каждого режима.
 * Здесь и в следующих классах (файлах) прошу ориентироваться на комментарии перед методами.
 * TODO: вынести методы-утилиты в отдельный класс. Сделать окончание режимов вместо break.
 * TODO: унифицировать switch/case!
 * TODO: допилить консольные методы ввода до актуальных GUI-методов.
 */

public class Main {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            String botName;
            LocalTime startTime;
            boolean useGui = true; // Переключатель GUI/консоль, для ввода рабочих окон, режима, времени старта.

            // Обнуляем файл счётчиков при первом запуске программы каждый день после 03:00 по Мск, так надо.
            ConfigManager.autoResetCounters();
            Map<String, String> config = ConfigManager.loadConfig(); // Загружаем конфиг из файла, при наличии

            // === Запрашиваем режим игры в режиме GUI/консоль ===
            int mode = useGui
                    ? Utilites.askModeGui()
                    : Utilites.askMode(scanner, config.get("mode"));

            // === Разворачиваем окна игры по заголовку. Каждое окно = "персы"/"боец") ===
            Utilites.restoreAllGameWindows();

            // === Получаем координаты и ширину каждого окна среди развёрнутых ===
            List<HWND> foundWindows = Utilites.findGameWindows();

            // === Запрашиваем в режиме GUI активные окна из числа найденных, с ними будет работать программа ===
            List<HWND> activeWindows = Utilites.askActiveWindows(foundWindows, config.get("activeWindows"));

            // === Читаем времена стартов из конфига (если есть). Не трогать, пока хоть как-то работает ===
            LocalTime arenaDefault = Utilites.parseTime(config.get("arena_start"));
            LocalTime kvDefault = Utilites.parseTime(config.get("kv_start"));
            LocalTime raidDefault = Utilites.parseTime(config.get("raid_start"));
            LocalTime tunnelDefault = Utilites.parseTime(config.get("tunnel_start"));

            // === Готовим переменные времени для записи обратно в конфиг. Не трогать, пока хоть как-то работает ===
            LocalTime arenaStart = arenaDefault;
            LocalTime kvStart = kvDefault;
            LocalTime raidStart = raidDefault;
            LocalTime tunnelStart = tunnelDefault;

            // === Запуск выбранного режима игры ===
            switch (mode) {
                case 1:
                    botName = "КВ";
                    startTime = useGui
                            ? Utilites.askStartTimeGui(botName, kvDefault)
                            : Utilites.askStartTime(scanner, botName, kvDefault);
                    kvStart = startTime;
                    ConfigManager.saveConfig(mode, activeWindows, arenaStart, kvStart, raidStart, tunnelStart);
                    ClanWarBot clanWarBot = new ClanWarBot(activeWindows, startTime, botName);
                    clanWarBot.start();
                    break;
                case 2:
                    botName = "Рейд";
                    startTime = useGui
                            ? Utilites.askStartTimeGui(botName, raidDefault)
                            : Utilites.askStartTime(scanner, botName, raidDefault);
                    raidStart = startTime;
                    ConfigManager.saveConfig(mode, activeWindows, arenaStart, kvStart, raidStart, tunnelStart);
                    RaidBot raidBot = new RaidBot(activeWindows, startTime, botName);
                    raidBot.start();
                    break;
                case 3:
                    botName = "Арена";
                    startTime = useGui
                            ? Utilites.askStartTimeGui(botName, arenaDefault)
                            : Utilites.askStartTime(scanner, botName, arenaDefault);
                    arenaStart = startTime;
                    ConfigManager.saveConfig(mode, activeWindows, arenaStart, kvStart, raidStart, tunnelStart);
                    ArenaBot arenaBot = new ArenaBot(activeWindows, startTime, botName);
                    arenaBot.start();
                    break;
                case 4:
                    botName = "Туннель";
                    startTime = useGui
                            ? Utilites.askStartTimeGui(botName, tunnelDefault)
                            : Utilites.askStartTime(scanner, botName, tunnelDefault);
                    tunnelStart = startTime;
                    ConfigManager.saveConfig(mode, activeWindows, arenaStart, kvStart, raidStart, tunnelStart);
                    TunnelBot tunnelBot = new TunnelBot(activeWindows, startTime, botName);
                    tunnelBot.start();
                    break;
                default:
                    System.out.println("Неизвестный режим. Завершаю.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}