package com.metrobot;

import com.sun.jna.platform.win32.WinDef;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.IOException;
import java.nio.file.*;
import java.time.*;

public class ConfigManager {
    private static final String CONFIG_FILE = "config.txt"; // не подходит для хранения в Resources
    private static final Path COUNTERS_FILE = Paths.get("counters.txt");
    private static final Path LAST_RESET_FILE = Paths.get("last_reset.txt");
    private static final ZoneId MOSCOW = ZoneId.of("Europe/Moscow");
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    // Загружаем конфиг из сервера/файла в Map. Сервер пока удалён, но всё с ним получилось!
    public static Map<String, String> loadConfig() {
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
    public static void saveConfig(int mode, List<WinDef.HWND> windows, LocalTime arenaStart, LocalTime kvStart,
                                  LocalTime raidStart, LocalTime tunnelStart) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CONFIG_FILE))) {
            pw.println("mode=" + mode);

            // Преобразуем HWND в индексы (1–4, не 0-3)
            List<Integer> windowInds = new ArrayList<>();
            for (WinDef.HWND hwnd : windows) {
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

    // Обнуляем файл счётчиков при первом запуске программы каждый день после 03:00 по Мск, так надо.
    public static void autoResetCounters() {
        try {
            LocalDate todayMsk = LocalDate.now(MOSCOW);
            LocalTime nowMsk = LocalTime.now(MOSCOW);

            // если файл с датой уже есть
            if (Files.exists(LAST_RESET_FILE)) {
                String last = Files.readString(LAST_RESET_FILE).trim();
                if (!last.isEmpty()) {
                    LocalDate lastDate = LocalDate.parse(last, DateTimeFormatter.ISO_LOCAL_DATE);
                    // если уже сбрасывали сегодня, ничего не делаем
                    if (lastDate.isEqual(todayMsk)) return;
                }
            }

            // если после 03:00 Мск — делаем сброс файла со счётчиками.
            if (nowMsk.isAfter(LocalTime.of(3, 0))) {
                System.out.println("Новый день после 03:00 МСК: counters.txt обнуляется");
                resetCounters();
                Files.writeString(LAST_RESET_FILE, todayMsk.toString());
            }

        } catch (Exception e) {
            System.err.println("Ошибка при автосбросе счётчиков: " + e.getMessage());
        }
    }

    private static void resetCounters() throws IOException {
        String defaultCounters = "Арена=0\nКВ=0\nРейд=0\n";
        if (!Files.exists(COUNTERS_FILE)) {
            Files.createFile(COUNTERS_FILE);
        }
        Files.writeString(COUNTERS_FILE, defaultCounters);
        System.out.println("Счётчики в файле counters.txt обнулены.");
    }

}
