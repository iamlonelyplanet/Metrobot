package com.metrobot;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/* "Чтобы понять, что происходит, надо вернуться на 2 года назад..." (с) Max Payne

Главный класс. Спрашивает в GUI/консоли: что за режим игры, каковы активные окна, что со временем старта каждого режима.
Класс получился безумно огромным, это косяк и некрасиво. Но если каждую некрасивость исправлять, то опять будет
бесконечное предрелизное состояние.
Зато отметил эти утилиты комментом "// === Дальше идут методы-утилиты для Main ===".
TODO: вынести методы-утилиты в отдельный класс. Сделать окончание режимов вместо break.
 */

public class Main {
    private static final String CONFIG_FILE = "config.txt"; // не подходит для хранения в Resources
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            boolean useGui = true; // Переключатель GUI/консоль, выбора рабочих окон, времени старта.
            Map<String, String> config = loadConfig(); // Загружаем конфиг при наличии

            // === Запрашиваем режим игры в режиме GUI/консоль. ===
            int mode;
            mode = useGui
                    ? askModeGui()
                    : askMode(scanner, config.get("mode"));

            // === Запрашиваем рабочие окна ("персы") в режиме GUI/консоль, от 1 до 4, потенциально не ограничено ===
            List<Integer> activeWindows;
            activeWindows = useGui
                    ? askActiveWindowsGui(config.get("activeWindows"))
                    : askActiveWindows(scanner, config.get("activeWindows"));

            // === Читаем времена стартов из конфига (если есть). Не трогать, пока хоть как-то работает ===
            LocalTime arenaDefault = parseTime(config.get("arena_start"));
            LocalTime kvDefault = parseTime(config.get("kv_start"));
            LocalTime raidDefault = parseTime(config.get("raid_start"));
            LocalTime tunnelDefault = parseTime(config.get("tunnel_start"));

            // === Готовим переменные времени для записи обратно в конфиг. Не трогать, пока хоть как-то работает ===
            LocalTime arenaStart = arenaDefault;
            LocalTime kvStart = kvDefault;
            LocalTime raidStart = raidDefault;
            LocalTime tunnelStart = tunnelDefault;

            // === Запуск выбранного режима игры ===
            // TODO: унифицировать!
            String botName;
            LocalTime startTime;
            switch (mode) {
                case 1:
                    botName = "КВ";
                    startTime = useGui
                            ? askStartTimeGui(botName, kvDefault)
                            : askStartTime(scanner, botName, kvDefault);
                    kvStart = startTime;
                    saveConfig(mode, activeWindows, arenaStart, kvStart, raidStart, tunnelStart);
                    ClanWarBot clanWarBot = new ClanWarBot(activeWindows, startTime, botName);
                    clanWarBot.start();
                    break;
                case 2:
                    botName = "Рейд";
                    startTime = useGui
                            ? askStartTimeGui(botName, raidDefault)
                            : askStartTime(scanner, botName, raidDefault);
                    raidStart = startTime;
                    saveConfig(mode, activeWindows, arenaStart, kvStart, raidStart, tunnelStart);
                    RaidBot raidBot = new RaidBot(activeWindows, startTime, botName);
                    raidBot.start();
                    break;
                case 3:
                    botName = "Арена";
                    startTime = useGui
                            ? askStartTimeGui(botName, arenaDefault)
                            : askStartTime(scanner, botName, arenaDefault);
                    arenaStart = startTime;
                    saveConfig(mode, activeWindows, arenaStart, kvStart, raidStart, tunnelStart);
                    ArenaBot arenaBot = new ArenaBot(activeWindows, startTime, botName);
                    arenaBot.start();
                    break;
                case 4:
                    botName = "Туннель";
                    startTime = useGui
                            ? askStartTimeGui(botName, tunnelDefault)
                            : askStartTime(scanner, botName, tunnelDefault);
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

    // === Дальше идут методы-утилиты для Main ===
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

    // Спрашиваем режим игры черезо консоль, с возможностью оставить по умолчанию
    private static int askMode(Scanner scanner, String defaultModeStr) {
        System.out.println("Выбери режим и введи цифру:");
        System.out.println("1. Клановые войны");
        System.out.println("2. Рейд");
        System.out.println("3. Арена");
        System.out.println("4. Туннели (бьём пауков и ящеров)");

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
            return 3; // По умолчанию будет Арена
        }
    }

    // Спрашиваем режим через GUI, с возможностью оставить по умолчанию
    private static int askModeGui() {
        String[] options = {"Клановые войны", "Рейд", "Арена", "Туннели"};
        int choice = javax.swing.JOptionPane.showOptionDialog(
                null,
                "Выбери режим:",
                "Метробот",
                javax.swing.JOptionPane.DEFAULT_OPTION,
                javax.swing.JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[2]
        );

        if (choice >= 0) { // при нажатии Esc = закрытии окна-диалога значение будет -1
            return (choice + 1); // индексы в "человеческий" формат 1–4 для понятности и совместимости
        } else {
            return 3; // По умолчанию будет Арена
        }
    }

    // Парсим время. TODO: разделители помимо двоеточия: точка? пробел?
    private static LocalTime parseTime(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            return LocalTime.parse(value, TIME_FORMAT);
        } catch (Exception e) {
            return null;
        }
    }

    // 2-3 метода подряд: ввод GUI или консоль. Запрашиваем время старта с дефолтным значением (при наличии).
    // Enter = оставить дефолт.
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

//    // Примитивный GUI-запрос времени старта. Тупо строка, некрасиво, но работало. Пока закомментировано.
//    private static LocalTime askStartTimeGui2(String botName, LocalTime defaultTime) {
//        while (true) {
//            String message;
//            if (defaultTime != null) {
//                message = "Введи время старта для режима " + botName +
//                        " (по умолчанию " + defaultTime.format(TIME_FORMAT) + "):";
//            } else {
//                message = "Введи время старта для режима " + botName +
//                        " (например 20:00):";
//            }
//
//            String input = javax.swing.JOptionPane.showInputDialog(null, message, "Метробот 2033",
//                    javax.swing.JOptionPane.QUESTION_MESSAGE);
//
//            if (input == null) {
//                if (defaultTime != null) return defaultTime;
//                else continue; // без времени по дефолту спрашиваем снова
//            }
//
//            input = input.trim();
//            if (input.isEmpty()) {
//                if (defaultTime != null) return defaultTime;
//                else continue;
//            }
//
//            try {
//                return LocalTime.parse(input, TIME_FORMAT);
//            } catch (Exception e) {
//                javax.swing.JOptionPane.showMessageDialog(null,
//                        "Неверный формат времени. Нужно ЧЧ:мм, например 20:45.",
//                        "Ошибка", javax.swing.JOptionPane.ERROR_MESSAGE);
//            }
//        }
//    }

    // Более продвинутый GUI-запрос времени старта. Основной способ.
    private static LocalTime askStartTimeGui(String botName, LocalTime defaultTime) {
        SpinnerDateModel model = new SpinnerDateModel(); // Оставить, несмотря на подчёркивания IDEA. Изучить.
        JSpinner spinner = new JSpinner(model);

        // Наконец-то паттерн HH:mm
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "HH:mm");
        spinner.setEditor(editor);

        // Если есть дефолтное время, то устанавливаем его. Не трогать, пока не изучил как следует!
        if (defaultTime != null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, defaultTime.getHour());
            cal.set(Calendar.MINUTE, defaultTime.getMinute());
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            spinner.setValue(cal.getTime());
        }

        int option = JOptionPane.showOptionDialog(
                null,
                spinner,
                "Введи время старта для режима " + botName,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, null, null
        );

        if (option == JOptionPane.OK_OPTION) {
            Date date = (Date) spinner.getValue();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        }

        // Если нажал Cancel, или крестик окна, то возвращаем дефолт
        return defaultTime;
    }

    // 2 метода подряд: ввод GUI или консоль. Запрашиваем список активных окон. Игровых окон может быть пока до 4,
    // но не все из них могут быть активными для работы программы! Так надо игрокам.
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

    private static List<Integer> askActiveWindowsGui(String defaultWindowsStr) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));

        JCheckBox[] boxes = {
                new JCheckBox("Окно 1"),
                new JCheckBox("Окно 2"),
                new JCheckBox("Окно 3"),
                new JCheckBox("Окно 4")
        };

        // Если есть дефолт — отмечаем соответствующие окна галочкой (GUI)
        if (defaultWindowsStr != null && !defaultWindowsStr.isEmpty()) {
            for (String part : defaultWindowsStr.split(" ")) {
                try {
                    int idx = Integer.parseInt(part.trim()) - 1; // индексация от 0
                    if (idx >= 0 && idx < boxes.length) {
                        boxes[idx].setSelected(true);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        for (JCheckBox box : boxes) {
            panel.add(box);
        }
        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Выбери активные окна",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        List<Integer> windows = getIntegers(defaultWindowsStr, result, boxes);
        return windows;
    }

    private static List<Integer> getIntegers(String defaultWindowsStr, int result, JCheckBox[] boxes) {
        List<Integer> windows = new ArrayList<>();
        if (result == JOptionPane.OK_OPTION) {
            for (int i = 0; i < boxes.length; i++) {
                if (boxes[i].isSelected()) {
                    windows.add(i + 1);
                }
            }
        } else if (defaultWindowsStr != null) {
            // Если нажали Cancel — вернуть дефолт
            for (String part : defaultWindowsStr.split(" ")) {
                try {
                    windows.add(Integer.parseInt(part.trim()));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return windows;
    }
}