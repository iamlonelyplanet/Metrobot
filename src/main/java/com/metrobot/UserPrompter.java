package com.metrobot;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class UserPrompter {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final boolean useGui;
    private final Scanner scanner;

    public UserPrompter(boolean useGui, Scanner scanner) {
        this.useGui = useGui;
        this.scanner = scanner;
    }

    public int askMode(String defaultModeStr) {
        return useGui ? askModeGui() : askModeConsole(defaultModeStr);
    }

    public List<Integer> askActiveWindows(String defaultWindowsStr) {
        return useGui ? askActiveWindowsGui(defaultWindowsStr) : askActiveWindowsConsole(defaultWindowsStr);
    }

    public LocalTime askStartTime(String botName, LocalTime defaultTime) {
        return useGui ? askStartTimeGui(botName, defaultTime) : askStartTimeConsole(botName, defaultTime);
    }

    // ====================
    // Консольные методы
    // ====================

    private int askModeConsole(String defaultModeStr) {
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
            return 3; // По умолчанию Арена
        }
    }

    private List<Integer> askActiveWindowsConsole(String defaultWindowsStr) {
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
            } catch (NumberFormatException ignored) {}
        }
        return windows;
    }

    private LocalTime askStartTimeConsole(String botName, LocalTime defaultTime) {
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

    // ====================
    // GUI методы
    // ====================

    private int askModeGui() {
        String[] options = {"Клановые войны", "Рейд", "Арена", "Туннели"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Выбери режим:",
                "Метробот",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[2]
        );

        if (choice >= 0) {
            return (choice + 1);
        } else {
            return 3; // По умолчанию Арена
        }
    }

    private List<Integer> askActiveWindowsGui(String defaultWindowsStr) {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JCheckBox[] boxes = {
                new JCheckBox("Окно 1"),
                new JCheckBox("Окно 2"),
                new JCheckBox("Окно 3"),
                new JCheckBox("Окно 4")
        };

        if (defaultWindowsStr != null && !defaultWindowsStr.isEmpty()) {
            for (String part : defaultWindowsStr.split(" ")) {
                try {
                    int idx = Integer.parseInt(part.trim()) - 1;
                    if (idx >= 0 && idx < boxes.length) boxes[idx].setSelected(true);
                } catch (NumberFormatException ignored) {}
            }
        }

        for (JCheckBox box : boxes) {
            panel.add(box);
        }

        int result = JOptionPane.showConfirmDialog(null, panel,
                "Выбери активные окна", JOptionPane.OK_CANCEL_OPTION);

        List<Integer> windows = new ArrayList<>();
        if (result == JOptionPane.OK_OPTION) {
            for (int i = 0; i < boxes.length; i++) {
                if (boxes[i].isSelected()) windows.add(i + 1);
            }
        }
        return windows;
    }

    private LocalTime askStartTimeGui(String botName, LocalTime defaultTime) {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "HH:mm");
        spinner.setEditor(editor);

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

        return defaultTime;
    }
}
