package com.metrobot;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String SAVE_FILE = "windows.json";

    public static void main(String[] args) {
        try {
            List<Integer> windows = loadSelectedWindows();

            if (windows.isEmpty()) {
                windows = askWindows();
                saveSelectedWindows(windows);
            }

            System.out.println("Выбраны окна: " + windows);
            ArenaBot bot = new ArenaBot(windows);
            bot.start(); // блокирующий вызов
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Integer> askWindows() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите номера окон для работы (через пробел). Доступные окна: 1, 2, 3, 4.");
        String line = scanner.nextLine().trim();
        List<Integer> res = new ArrayList<>();
        if (line.isEmpty()) return res;
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
            new Gson().toJson(numbers, writer);
            System.out.println("Сохранён " + SAVE_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Integer> loadSelectedWindows() {
        File f = new File(SAVE_FILE);
        if (!f.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(f)) {
            Type listType = new TypeToken<List<Integer>>() {
            }.getType();
            List<Integer> list = new Gson().fromJson(reader, listType);
            return list == null ? new ArrayList<>() : list;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
