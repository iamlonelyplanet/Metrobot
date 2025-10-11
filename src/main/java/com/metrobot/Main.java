package com.metrobot;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.HWND;

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

            restoreAllGameWindows();
            List<WinDef.HWND> foundWindows = findGameWindows();

            // === Запрашиваем рабочие окна ("персы") в режиме GUI/консоль, от 1 до 4, потенциально не ограничено ===
            List<HWND> activeWindows;
            activeWindows = askActiveWindows(foundWindows, config.get("activeWindows"));

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
    /* Ищем в Windows все окна с названием игры. Первый вариант для соцсети МойМир, второй для ВКонтакте. Затем
    сортируем список в порядке: верх - слева направо, низ - слева направо.
    */
    protected static List<WinDef.HWND> findGameWindows() {
        User32 user32 = User32.INSTANCE;
        List<WinDef.HWND> found = new ArrayList<>();

        user32.EnumWindows((hWnd, data) -> {
            char[] buffer = new char[512];
            user32.GetWindowText(hWnd, buffer, 512);
            String title = new String(buffer).trim();
            if (title.contains("Игроклуб") || title.contains("2033")) {
                found.add(hWnd);
            }
            return true;
        }, null);

        // сортируем по координатам
        found.sort((h1, h2) -> {
            RECT r1 = new RECT();
            RECT r2 = new RECT();
            user32.GetWindowRect(h1, r1);
            user32.GetWindowRect(h2, r2);
            if (r1.top != r2.top) {
                return Integer.compare(r1.top, r2.top);
            } else {
                return Integer.compare(r1.left, r2.left);
            }
        });

        // создаём список на 4 окна (возможные позиции)
        List<WinDef.HWND> ordered = new ArrayList<>(Arrays.asList(null, null, null, null));

        // определяем разрешение монитора и примерное расположение окон на экране
        int screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

        int midX = screenWidth / 2;
        int midY = screenHeight / 2;

        for (WinDef.HWND hWnd : found) {
            RECT r = new RECT();
            user32.GetWindowRect(hWnd, r);

            int centerX = (r.left + r.right) / 2;
            int centerY = (r.top + r.bottom) / 2;

            boolean top = centerY < midY;
            boolean left = centerX < midX;

            int index;
            if (top && left) index = 0;       // Окно 1
            else if (top) index = 1;          // Окно 2
            else if (left) index = 2;         // Окно 3
            else index = 3;                   // Окно 4

            ordered.set(index, hWnd);
        }

        System.out.println("=== Найденные игровые окна (позиции 1–4) ===");
        for (int i = 0; i < 4; i++) {
            if (ordered.get(i) != null) {
                RECT r = new RECT();
                user32.GetWindowRect(ordered.get(i), r);
                System.out.printf("Окно %d: (%d, %d) — (%d, %d)%n",
                        i + 1, r.left, r.top, r.right, r.bottom);
            } else {
                System.out.printf("Окно %d: [не найдено]%n", i + 1);
            }
        }

        return ordered;
    }

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
    private static void saveConfig(int mode, List<HWND> windows, LocalTime arenaStart, LocalTime kvStart,
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

    // Спрашиваем режим игры через GUI, с возможностью оставить по умолчанию
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

    // 2 метода подряд: ввод GUI или консоль. Запрашиваем время старта с дефолтным значением (при наличии).
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

    // GUI-запрос времени старта, при помощи окна-спиннера
    private static LocalTime askStartTimeGui(String botName, LocalTime defaultTime) {
        SpinnerDateModel model = new SpinnerDateModel(); // Оставить, несмотря на подчёркивания IDEA. Изучить.
        JSpinner spinner = new JSpinner(model);

        // Наконец-то HH:mm!
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

    /* Спрашиваем список активных окон, основываясь на автоматически найденных. Игровых окон может быть пока до 4.
    Некоторые из найденных окон могут быть неактивными, пусть такие работают сами, без участия программы. Так надо.
     */
    private static List<WinDef.HWND> askActiveWindows(List<WinDef.HWND> foundWindows, String defaultWindowsStr) {
        User32 user32 = User32.INSTANCE;
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));

        JCheckBox[] boxes = new JCheckBox[foundWindows.size()];

        // Формируем подписи с координатами найденных окон
        for (int i = 0; i < foundWindows.size(); i++) {
            String label;
            WinDef.HWND hWnd = foundWindows.get(i);
            if (hWnd != null) {
                WinDef.RECT r = new WinDef.RECT();
                user32.GetWindowRect(hWnd, r);
                label = String.format("Окно %d: (%d, %d) — (%d, %d)", i + 1, r.left, r.top, r.right, r.bottom);
            } else {
                label = String.format("Окно %d: [не найдено]", i + 1);
            }
            boxes[i] = new JCheckBox(label);
            if (hWnd == null) boxes[i].setEnabled(false); // нельзя выбрать несуществующее окно
            panel.add(boxes[i]);
        }

        // Если есть дефолт — отмечаем соответствующие окна
        if (defaultWindowsStr != null && !defaultWindowsStr.isEmpty()) {
            for (String part : defaultWindowsStr.split(" ")) {
                try {
                    int idx = Integer.parseInt(part.trim()) - 1;
                    if (idx >= 0 && idx < boxes.length && boxes[idx].isEnabled()) {
                        boxes[idx].setSelected(true);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Найдены игровые окна. С какими работаем?",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        return getSelectedWindows(foundWindows, defaultWindowsStr, result, boxes);
    }

    private static List<WinDef.HWND> getSelectedWindows(List<WinDef.HWND> foundWindows,
                                                        String defaultWindowsStr,
                                                        int result,
                                                        JCheckBox[] boxes) {
        List<WinDef.HWND> selected = new ArrayList<>();

        if (result == JOptionPane.OK_OPTION) {
            for (int i = 0; i < boxes.length; i++) {
                if (boxes[i].isSelected() && foundWindows.get(i) != null) {
                    selected.add(foundWindows.get(i));
                }
            }
        } else if (defaultWindowsStr != null && !defaultWindowsStr.isEmpty()) {
            // Если нажали Cancel — восстановим из конфига
            for (String part : defaultWindowsStr.split(" ")) {
                try {
                    int idx = Integer.parseInt(part.trim()) - 1;
                    if (idx >= 0 && idx < foundWindows.size() && foundWindows.get(idx) != null) {
                        selected.add(foundWindows.get(idx));
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return selected;
    }

    private static void restoreAllGameWindows() {
        User32 user32 = User32.INSTANCE;

        user32.EnumWindows((hWnd, data) -> {
            char[] buffer = new char[512];
            user32.GetWindowText(hWnd, buffer, 512);
            String title = new String(buffer).trim();

            if (title.contains("Игроклуб") || title.contains("2033")) {
                user32.ShowWindow(hWnd, User32.SW_RESTORE);
            }
            return true;
        }, null);
    }
}