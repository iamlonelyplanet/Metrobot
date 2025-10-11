package com.metrobot;

import com.sun.jna.platform.win32.WinDef;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    private static final String CONFIG_FILE = "config.txt"; // не подходит для хранения в Resources
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
}
