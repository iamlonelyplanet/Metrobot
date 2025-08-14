package com.metrobot;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String SAVE_FILE = "windows.txt";

    public static void main(String[] args) {
        try {
            List<Integer> lastConfig = loadSelectedWindows();
            List<Integer> windows = askWindows(lastConfig);
            saveSelectedWindows(windows);

            System.out.println("Выбраны окна: " + windows);
            ArenaBot bot = new ArenaBot(windows);
            bot.start(); // блокирующий вызов
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Integer> askWindows(List<Integer> lastConfig) {
        Scanner scanner = new Scanner(System.in);

        if (!lastConfig.isEmpty()) {
            System.out.println("Последняя конфигурация окон: " + lastConfig);
        } else {
            System.out.println("Последняя конфигурация отсутствует.");
        }

        System.out.println("Введи номера окон для работы (через пробел), или Enter, чтобы оставить как есть:");
        String line = scanner.nextLine().trim();

        if (line.isEmpty() && !lastConfig.isEmpty()) {
            return lastConfig;
        }

        List<Integer> res = new ArrayList<>();
        for (String tok : line.split("\\s+")) {
            try {
                int v = Integer.parseInt(tok);
                if (v >= 1 && v <= 4) res.add(v);
            } catch (NumberFormatException ignored) {
            }
        }
        return res;
    }

    private static void saveSelectedWindows(List<Integer> numbers) {
        try (Writer writer = new FileWriter(SAVE_FILE)) {
            for (int num : numbers) {
                writer.write(num + " ");
            }
            System.out.println("Сохранено в " + SAVE_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Integer> loadSelectedWindows() {
        File f = new File(SAVE_FILE);
        if (!f.exists()) return new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line = reader.readLine();
            List<Integer> list = new ArrayList<>();
            if (line != null && !line.trim().isEmpty()) {
                for (String tok : line.trim().split("\\s+")) {
                    try {
                        list.add(Integer.parseInt(tok));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
