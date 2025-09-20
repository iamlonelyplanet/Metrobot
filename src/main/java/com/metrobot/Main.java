package com.metrobot;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    private static final String CONFIG_FILE = "config/config.txt";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            // === Загружаем конфиг, если есть ===
            Map<String, String> config = loadConfig();

            // === Спрашиваем режим игры ===
            int mode = askMode(scanner, config.get("mode"));

            // === Спрашиваем рабочие окна ("персы"), от 1 до 4, потенциально не ограничено ===
            List<Integer> activeWindows = askActiveWindows(scanner, config.get("activeWindows"));
            if (activeWindows.isEmpty()) {
                System.out.println("Окна не выбраны — выхожу.");
                return;
            }

            // === Читаем времена стартов из конфига (если есть) ===
            LocalTime arenaDefault = parseTime(config.get("arena_start"));
            LocalTime kvDefault = parseTime(config.get("kv_start"));
            LocalTime raidDefault = parseTime(config.get("raid_start"));
            LocalTime tunnelDefault = parseTime(config.get("tunnel_start"));

            // Подготовим переменные для записи обратно в конфиг
            LocalTime arenaStart = arenaDefault;
            LocalTime kvStart = kvDefault;
            LocalTime raidStart = raidDefault;
            LocalTime tunnelStart = tunnelDefault;

            // === Запуск бота в зависимости от режима игры ===
            String botName;
            LocalTime startTime;
            switch (mode) {
                case 1:
                    botName = "КВ";
                    startTime = askStartTime(scanner, botName, kvDefault);

                    kvStart = startTime;
                    saveConfig(mode, activeWindows, arenaStart, kvStart, raidStart, tunnelStart);

                    ClanWarBot clanWarBot = new ClanWarBot(activeWindows, startTime, botName);
                    clanWarBot.start();
                    break;
                case 2:
                    botName = "Рейд";
                    startTime = askStartTime(scanner, botName, raidDefault);
                    raidStart = startTime;

                    saveConfig(mode, activeWindows, arenaStart, kvStart, raidStart, tunnelStart);

                    RaidBot raidBot = new RaidBot(activeWindows, startTime, botName);
                    raidBot.start();
                    break;
                case 3:
                    botName = "Арена";
                    startTime = askStartTime(scanner, botName, arenaDefault);

                    arenaStart = startTime;
                    saveConfig(mode, activeWindows, arenaStart, kvStart, raidStart, tunnelStart);

                    ArenaBot arenaBot = new ArenaBot(activeWindows, startTime, botName);
                    arenaBot.start();
                    break;
                case 4:
                    botName = "Туннель"; // TODO: посмотреть грамматику
                    startTime = askStartTime(scanner, botName, tunnelDefault);

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

    // Дальше идут методы-утилиты

    // Загружаем конфиг из файла в Map
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

    // Спрашиваем режим с возможностью оставить по умолчанию
    private static int askMode(Scanner scanner, String defaultModeStr) {
        System.out.println("Выбери режим и введи цифру:");
        System.out.println("1. Клановые войны");
        System.out.println("2. Рейд");
        System.out.println("3. Арена");
        System.out.println("4. Туннели (20 пауков + 40 ящеров, " +
                "старт с Парка Культуры (Ганза), нет работы у Начстанции)");

        if (defaultModeStr != null) {
            System.out.println("(Enter для выбора по умолчанию: " + defaultModeStr + ")");
        }

        String input = scanner.nextLine().trim();
        if (input.isEmpty() && defaultModeStr != null) {
            return Integer.parseInt(defaultModeStr);
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    // Сохраняем конфиг в файл
    private static void saveConfig(int mode, List<Integer> windows, LocalTime arenaStart, LocalTime kvStart,
                                   LocalTime raidStart, LocalTime tunnelStart) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CONFIG_FILE))) {
            pw.println("mode=" + mode);
            pw.println("activeWindows=" + windows.toString().replaceAll("[\\[\\],]", ""));
            if (arenaStart != null) pw.println("arena_start=" + arenaStart.format(TIME_FORMAT));
            if (kvStart != null) pw.println("kv_start=" + kvStart.format(TIME_FORMAT));
            if (raidStart != null) pw.println("raid_start=" + raidStart.format(TIME_FORMAT));
            if (tunnelStart != null) pw.println("tunnel_start=" + tunnelStart.format(TIME_FORMAT));
        } catch (IOException e) {
            System.err.println("Ошибка записи " + CONFIG_FILE + ": " + e.getMessage());
        }
    }

    // Парсим время. TODO: разделители.
    private static LocalTime parseTime(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            return LocalTime.parse(value, TIME_FORMAT);
        } catch (Exception e) {
            return null;
        }
    }

    // Спрашиваем время старта с дефолтным значением (если есть). Enter = оставить дефолт.
    private static LocalTime askStartTime(Scanner scanner, String botName, LocalTime defaultTime) {
        while (true) {
            if (defaultTime != null) {
                System.out.print("Введи время старта для режима " + botName + " (по умолчанию " +
                        defaultTime.format(TIME_FORMAT) + "): ");
            } else {
                System.out.print("Введи время старта для режима " + botName + " (например 20:00): ");
            }
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                if (defaultTime != null) return defaultTime;
                System.out.println("Время обязательно. Давай ещё раз?");
                continue;
            }
            try {
                return LocalTime.parse(input, TIME_FORMAT);
            } catch (Exception e) {
                System.out.println("Неверный формат времени, ожидалось HH:mm (ЧЧ:мм). Давай ещё раз?");
            }
        }
    }

    // Спрашиваем список окон
    private static List<Integer> askActiveWindows(Scanner scanner, String defaultWindowsStr) {
        if (defaultWindowsStr != null) {
            System.out.print("Введи номера окон через пробел. В прошлый раз были [" + defaultWindowsStr + "]: ");
        } else {
            System.out.print("Введи номера окон через пробел. Доступные: 1, 2, 3, 4: ");
        }

        String input = scanner.nextLine().trim();
        if (input.isEmpty() && defaultWindowsStr != null) input = defaultWindowsStr;

        List<Integer> windows = new ArrayList<>();
        for (String part : input.split(" ")) {
            try {
                windows.add(Integer.parseInt(part.trim()));
            } catch (NumberFormatException ignored) {
            }
        }
        return windows;
    }
}
