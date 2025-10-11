package com.metrobot;

import java.awt.*;
import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import com.sun.jna.platform.win32.WinDef.HWND;

import javax.swing.*;

/**
 * Главный класс. Спрашивает в GUI/консоли: режим игры, активные окна, временя старта каждого режима.
 * Класс получился огромным, это косяк и некрасиво. Но если каждую некрасивость исправлять, то опять будет
 * бесконечное предрелизное состояние.
 * Зато отметил эти утилиты комментом "// === Методы-утилиты для Main ===".
 * TODO: вынести методы-утилиты в отдельный класс. Сделать окончание режимов вместо break.
 */

public class Main {
    private static final String CONFIG_FILE = "config.txt"; // не подходит для хранения в Resources
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            String botName;
            LocalTime startTime;
            boolean useGui = true; // Переключатель GUI/консоль, для ввода рабочих окон, режима, времени старта.
            Map<String, String> config = loadConfig(); // Загружаем конфиг из файла, при наличии

            // === Запрашиваем режим игры в режиме GUI/консоль. ===
            int mode = useGui
                    ? Utilites.askModeGui()
                    : Utilites.askMode(scanner, config.get("mode"));

            Utilites.restoreAllGameWindows();
            List<HWND> foundWindows = Utilites.findGameWindows();

            // === Запрашиваем рабочие окна ("персы", "бойцы") от 1 до 4, только в режиме GUI ===
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
            // TODO: унифицировать!
            switch (mode) {
                case 1:
                    botName = "КВ";
                    startTime = useGui
                            ? Utilites.askStartTimeGui(botName, kvDefault)
                            : Utilites.askStartTime(scanner, botName, kvDefault);
                    kvStart = startTime;
                    saveConfig(mode, activeWindows, arenaStart, kvStart, raidStart, tunnelStart);
                    ClanWarBot clanWarBot = new ClanWarBot(activeWindows, startTime, botName);
                    clanWarBot.start();
                    break;
                case 2:
                    botName = "Рейд";
                    startTime = useGui
                            ? Utilites.askStartTimeGui(botName, raidDefault)
                            : Utilites.askStartTime(scanner, botName, raidDefault);
                    raidStart = startTime;
                    saveConfig(mode, activeWindows, arenaStart, kvStart, raidStart, tunnelStart);
                    RaidBot raidBot = new RaidBot(activeWindows, startTime, botName);
                    raidBot.start();
                    break;
                case 3:
                    botName = "Арена";
                    startTime = useGui
                            ? Utilites.askStartTimeGui(botName, arenaDefault)
                            : Utilites.askStartTime(scanner, botName, arenaDefault);
                    arenaStart = startTime;
                    saveConfig(mode, activeWindows, arenaStart, kvStart, raidStart, tunnelStart);
                    ArenaBot arenaBot = new ArenaBot(activeWindows, startTime, botName);
                    arenaBot.start();
                    break;
                case 4:
                    botName = "Туннель";
                    startTime = useGui
                            ? Utilites.askStartTimeGui(botName, tunnelDefault)
                            : Utilites.askStartTime(scanner, botName, tunnelDefault);
                    tunnelStart = startTime;
                    saveConfig(mode, activeWindows, arenaStart, kvStart, raidStart, tunnelStart);
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

    // === Методы-утилиты для Main ===
    // Загружаем конфиг из сервера/файла в Map. Сервер пока удалён, но всё с ним получилось!
    private static Map<String, String> loadConfig() {
        Map<String, String> config = new HashMap<>();
        File file = new File(CONFIG_FILE);
        if (!file.exists()) return config;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    config.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка чтения " + CONFIG_FILE + ": " + e.getMessage());
        }
        return config;
    }

    // Сохраняем конфиг в локальный файл, без взаимодействия с сервером
    private static void saveConfig(int mode, List<HWND> windows, LocalTime arenaStart, LocalTime kvStart,
                                   LocalTime raidStart, LocalTime tunnelStart) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CONFIG_FILE))) {
            pw.println("mode=" + mode);

            // Преобразуем HWND в индексы (1–4, не 0-3)
            List<Integer> windowInds = new ArrayList<>();
            for (HWND hwnd : windows) {
                int index = windows.indexOf(hwnd) + 1;
                if (index > 0) windowInds.add(index);
            }
            pw.println("activeWindows=" + windowInds.toString().replaceAll("[\\[\\],]", ""));

            if (arenaStart != null) pw.println("arena_start=" + arenaStart.format(TIME_FORMAT));
            if (kvStart != null) pw.println("kv_start=" + kvStart.format(TIME_FORMAT));
            if (raidStart != null) pw.println("raid_start=" + raidStart.format(TIME_FORMAT));
            if (tunnelStart != null) pw.println("tunnel_start=" + tunnelStart.format(TIME_FORMAT));
        } catch (IOException e) {
            System.err.println("Ошибка записи " + CONFIG_FILE + ": " + e.getMessage());
        }
    }
}