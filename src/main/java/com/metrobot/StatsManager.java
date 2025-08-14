package com.metrobot;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Простая менеджер-статистики, сохраняет JSON-подобный файл в data/stats-YYYY-MM-DD.json
 */
public class StatsManager {
    private static final Path DATA_DIR = Paths.get("data");
    private static final DateTimeFormatter DATEFMT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final LocalDate currentDate;
    private final Path statsFile;
    private Map<String, Integer> counts = new LinkedHashMap<>();
    private int launchesToday = 0;
    private LocalDateTime lastLaunch;

    public StatsManager() throws IOException {
        if (!Files.exists(DATA_DIR)) Files.createDirectories(DATA_DIR);
        this.currentDate = LocalDate.now(); // системная локаль
        this.statsFile = DATA_DIR.resolve("stats-" + currentDate.format(DATEFMT) + ".json");
        loadOrCreate();
    }

    private void loadOrCreate() throws IOException {
        if (Files.exists(statsFile)) {
            String content = new String(Files.readAllBytes(statsFile), StandardCharsets.UTF_8).trim();
            // Очень простой парсер — ожидаем ключи и числа. Не используем сторонние JSON libs.
            // Формат записи делаем таким:
            // launches=2
            // lastLaunch=2025-08-12T09:33:00
            // F1=12
            // Лёха-156=8
            counts.clear();
            for (String line : content.split("\\R")) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (line.startsWith("launches=")) {
                    launchesToday = Integer.parseInt(line.substring("launches=".length()));
                } else if (line.startsWith("lastLaunch=")) {
                    lastLaunch = LocalDateTime.parse(line.substring("lastLaunch=".length()));
                } else if (line.contains("=")) {
                    String[] kv = line.split("=", 2);
                    counts.put(kv[0], Integer.parseInt(kv[1]));
                }
            }
        } else {
            // default
            launchesToday = 0;
            lastLaunch = LocalDateTime.now();
            // инициализируем счётчики для всех окон
            for (WindowConfig.GameWindow w : WindowConfig.defaultWindows().values()) {
                counts.put(w.name, 0);
            }
            save();
        }
    }

    // увеличиваем счётчик для окна
    public synchronized void incrementWindow(String windowName) {
        counts.putIfAbsent(windowName, 0);
        counts.put(windowName, counts.get(windowName) + 1);
        saveQuiet();
    }

    public synchronized void incrementLaunches() {
        launchesToday++;
        lastLaunch = LocalDateTime.now();
        saveQuiet();
    }

    public synchronized Map<String, Integer> getCounts() {
        return new LinkedHashMap<>(counts);
    }

    public synchronized int getLaunchesToday() {
        return launchesToday;
    }

    private void saveQuiet() {
        try { save(); } catch (IOException e) { e.printStackTrace(); }
    }

    public synchronized void save() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("launches=").append(launchesToday).append("\n");
        sb.append("lastLaunch=").append(lastLaunch == null ? LocalDateTime.now().toString() : lastLaunch.toString()).append("\n");
        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            sb.append(e.getKey()).append("=").append(e.getValue()).append("\n");
        }
        Files.write(statsFile, sb.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    // проверка, нужно ли обнулить стату (в 05:00 новый день)
    public boolean isNewDayAndAfter5am() {
        LocalDate now = LocalDate.now();
        if (!now.equals(currentDate)) { // дата сменилась
            LocalTime nowt = LocalTime.now();
            return nowt.isAfter(LocalTime.of(5,0));
        }
        return false;
    }

    // сбросить счётчики (использовать когда начинается новый игровой день)
    public synchronized void resetForNewDay() throws IOException {
        launchesToday = 0;
        lastLaunch = LocalDateTime.now();
        counts.clear();
        for (WindowConfig.GameWindow w : WindowConfig.defaultWindows().values()) {
            counts.put(w.name, 0);
        }
        save();
    }
}
