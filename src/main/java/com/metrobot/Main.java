package com.metrobot;

import java.io.*;
import java.time.LocalTime;
import java.util.*;

public class Main {

    private static final String CONFIG_FILE = "Config.txt";

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            // Загружаем конфиг, если есть
            Map<String, String> config = loadConfig();

            // === Спрашиваем режим игры ===
            int mode = askMode(scanner, config.get("mode"));

            // === Окна ===
            List<Integer> windows = askWindows(scanner, config.get("windows"));
            if (windows.isEmpty()) {
                System.out.println("Окна не выбраны — выхожу.");
                return;
            }

            // Сохраняем выбранные настройки обратно в файл
            saveConfig(mode, windows);

            // === Запуск бота ===
            String botName;
            LocalTime startTime;
            switch (mode) {
                case 1:
                    botName = "КВ";
                    startTime = askStartTime(scanner, botName);
                    ClanWarBot clanWarBot = new ClanWarBot(windows, startTime);
                    clanWarBot.start();
                    break;
                case 2:
                    botName = "Рейд";
                    startTime = askStartTime(scanner, botName);
                    RaidBot raidBot = new RaidBot(windows, startTime);
                    raidBot.start();
                    break;
                case 3:
                    botName = "Арена";
                    startTime = askStartTime(scanner, botName);
                    ArenaBot arenaBot = new ArenaBot(windows, startTime);
                    arenaBot.start();
                    break;
                default:
                    System.out.println("Неизвестный режим. Завершаю.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Загружаем конфиг из файла
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

    // Сохраняем конфиг
    private static void saveConfig(int mode, List<Integer> windows) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CONFIG_FILE))) {
            pw.println("mode=" + mode);
            pw.println("windows=" + windows.toString().replaceAll("[\\[\\],]", ""));
        } catch (IOException e) {
            System.err.println("Ошибка записи " + CONFIG_FILE + ": " + e.getMessage());
        }
    }

    // Спрашиваем режим с возможностью оставить по умолчанию
    private static int askMode(Scanner scanner, String defaultModeStr) {
        System.out.println("Выбери режим:");
        System.out.println("1. Клановые войны");
        System.out.println("2. Рейд");
        System.out.println("3. Арена");

        int defaultMode = -1;
        if (defaultModeStr != null) {
            try {
                defaultMode = Integer.parseInt(defaultModeStr);
            } catch (NumberFormatException ignored) {}
        }

        if (defaultMode != -1) {
            System.out.print("Введи номер режима игры (Enter - оставить имеющиеся настройки) [" + defaultMode + "]: ");
        } else {
            System.out.print("Введи номер режима игры (Enter - оставить имеющиеся настройки): ");
        }

        String line = scanner.nextLine().trim();
        if (line.isEmpty() && defaultMode != -1) {
            return defaultMode;
        }

        return Integer.parseInt(line);
    }

    // Спрашиваем окна с возможностью оставить по умолчанию
    private static List<Integer> askWindows(Scanner scanner, String defaultWindowsStr) {
        List<Integer> res = new ArrayList<>();
        List<Integer> defaultWindows = new ArrayList<>();

        if (defaultWindowsStr != null && !defaultWindowsStr.isEmpty()) {
            for (String tok : defaultWindowsStr.split("\\s+")) {
                try {
                    int v = Integer.parseInt(tok);
                    if (v >= 1 && v <= 4) defaultWindows.add(v);
                } catch (NumberFormatException ignored) {}
            }
        }

        if (!defaultWindows.isEmpty()) {
            System.out.print("Введи номера окон (через пробел) [" + defaultWindowsStr + "]: ");
        } else {
            System.out.print("Введи номера окон (через пробел). Доступные: 1, 2, 3, 4: ");
        }

        String line = scanner.nextLine().trim();
        if (line.isEmpty() && !defaultWindows.isEmpty()) {
            return defaultWindows;
        }

        for (String tok : line.split("\\s+")) {
            try {
                int v = Integer.parseInt(tok);
                if (v >= 1 && v <= 4) res.add(v);
            } catch (NumberFormatException ignored) {}
        }
        return res;
    }

    // Спрашиваем время старта режима
    private static LocalTime askStartTime(Scanner scanner, String botName) {
        System.out.printf("Введи время старта режима %s (например 18:05): ", botName);
        String input = scanner.nextLine().trim();
        return LocalTime.parse(input); // бросит исключение, если формат неверный
    }
}
