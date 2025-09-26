package com.metrobot;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CounterStorage {
    private static final String FILE_NAME = "counters.txt";

    // Загружаем счётчики из файла
    public static Map<String, Counter> loadCounters(List<String> names) {
        Map<String, Counter> counters = new HashMap<>();

        // Инициализируем нулями на случай отсутствия файла
        for (String name : names) {
            counters.put(name, new Counter(name, 0));
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_NAME));
            for (String line : lines) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    int value = Integer.parseInt(parts[1].trim());
                    if (counters.containsKey(name)) {
                        counters.get(name).setCount(value);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Файл счётчиков не найден, будут использованы значения по умолчанию (0).");
        }
        return counters;
    }

    // Сохраняем счётчики в файл
    public static void saveCounters(Map<String, Counter> counters) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Counter counter : counters.values()) {
                writer.println(counter.getName() + "=" + counter.getCount());
            }
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении счётчиков: " + e.getMessage());
        }
    }
}
