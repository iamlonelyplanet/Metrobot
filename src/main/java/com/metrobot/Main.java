package com.metrobot;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Выберите режим:");
            System.out.println("1. Клановые войны");
            System.out.println("2. Рейд");
            System.out.println("3. Арена");
            System.out.print("Введите номер режима игры: ");
            int mode = Integer.parseInt(scanner.nextLine().trim());

            List<Integer> windows = askWindows();
            if (windows.isEmpty()) {
                System.out.println("Окна не выбраны — выхожу.");
                return;
            }

            switch (mode) {
                case 1:
                    ClanWarBot clanWarBot = new ClanWarBot(windows);
                    clanWarBot.start(); // блокирующий запуск
                    break;
                case 2:
                    System.out.print("Введи время старта рейда (например 18:05): ");
                    String timeStr = scanner.nextLine().trim();

                    RaidBot raidBot = new RaidBot(windows, timeStr);
                    raidBot.start(); // блокирующий запуск
                    break;
                case 3:
                    System.out.print("Идём на Арену!");

                    ArenaBot arenaBot = new ArenaBot(windows);
                    arenaBot.start(); // блокирующий запуск
                    break;
                default:
                    System.out.println("Неизвестный режим. Завершаю.");
            }

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
}
