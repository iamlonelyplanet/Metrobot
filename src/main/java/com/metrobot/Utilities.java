package com.metrobot;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * Вспомогательные методы для GUI запросов (режим, окна, время).
 */

public class Utilities {
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    public static boolean usePet = false;

    public record ModeSelection(int mode, boolean usePet) {
    }


    // Спрашиваем режим игры через GUI, с возможностью оставить по умолчанию
    public static int askMode() {
        String[] options = {"Клановые войны", "Рейд", "Арена", "Туннели", "Крысы"};

        JComboBox<String> modeCombo = new JComboBox<>(options);
        modeCombo.setSelectedIndex(2); // по умолчанию Арена

        JCheckBox petCheck = new JCheckBox("С питомцем");

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Выбери режим:"));
        panel.add(modeCombo);
        panel.add(petCheck);

        // Локальная функция обновления чекбокса по выбранному режиму
        Runnable applyState = () -> {
            int idx = modeCombo.getSelectedIndex();
            switch (idx) {
                case 0: // Клановые войны
                case 1: // Рейд
                    petCheck.setSelected(false);
                    petCheck.setEnabled(false);
                    break;
                case 2: // Арена
                    petCheck.setSelected(false);
                    petCheck.setEnabled(true);
                    break;
                case 3: // Туннели
                    petCheck.setSelected(true);
                    petCheck.setEnabled(true);
                    break;
                case 4: // Крысы
                    petCheck.setSelected(true);
                    petCheck.setEnabled(true);
                    break;
            }
        };

        applyState.run(); // первичная инициализация
        modeCombo.addActionListener(e -> applyState.run());

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Метробот",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            int idx = modeCombo.getSelectedIndex();
            // Флажок учитывается только для Арены/Туннелей/Крыс
            usePet = (idx == 2 || idx == 3 || idx == 4) && petCheck.isSelected();
            return idx + 1; // 1–5, в человеческом режиме
        } else {
            // Esc/Cancel -> выход из программы (-1), подхватывается null.
            usePet = false;
            return -1;
        }
    }

    public static ModeSelection askModeSelection() {
        int mode = askMode();      // старый метод
        if (mode <= 0) {
            return null;
        }
        boolean pet = usePet;      // старое статическое поле
        return new ModeSelection(mode, pet);
    }


    // Парсим время. TODO: разделители помимо двоеточия: точка? пробел?
    public static LocalTime parseTime(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            return LocalTime.parse(value, TIME_FORMAT);
        } catch (Exception e) {
            return null;
        }
    }

    // GUI-запрос времени старта, при помощи окна-спиннера с дефолтным значением (при наличии). Enter = оставить дефолт.
    public static LocalTime askStartTime(String botName, LocalTime defaultTime) {
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

        // Если нажал Cancel, или крестик окна, то отмена запуска
        return null;
    }

    /**
     * Сужаем размер окна до минимально возможного, 1033 на 768
     */
    public static void restoreAllGameWindows() {
        User32 user32 = User32.INSTANCE;

        user32.EnumWindows((hWnd, data) -> {
            char[] buffer = new char[512];
            user32.GetWindowText(hWnd, buffer, 512);
            String title = new String(buffer).trim();

            if (title.contains("Игроклуб") || title.contains("2033")) {
                resizeWindows(hWnd);
                user32.ShowWindow(hWnd, User32.SW_RESTORE);
            }
            return true;
        }, null);
    }

    public static void resizeWindows(HWND hwnd) {
        WinDef.RECT r = new WinDef.RECT();
        User32.INSTANCE.GetWindowRect(hwnd, r);
        if (r.right - r.left == Buttons.windowWidth) {
            return;
        }

        User32.INSTANCE.SetWindowPos(
                hwnd,
                null,
                r.left,        // сохраняем позицию
                r.top,
                Buttons.windowWidth,
                Buttons.windowHeight,
                WinUser.SWP_NOZORDER | WinUser.SWP_SHOWWINDOW
        );
    }

    /**
     * Спрашиваем список активных окон, основываясь на автоматически найденных. Игровых окон может быть пока до 4.
     * Некоторые из найденных окон могут быть неактивными, пусть такие работают сами, без участия программы. Так надо.
     */
    public static List<HWND> askActiveWindows(List<HWND> foundWindows, String defaultWindowsStr) {
        User32 user32 = User32.INSTANCE;
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));

        JCheckBox[] boxes = new JCheckBox[foundWindows.size()];

        // Формируем подписи с координатами найденных окон
        for (int i = 0; i < foundWindows.size(); i++) {
            String label;
            HWND hWnd = foundWindows.get(i);
            if (hWnd != null) {
                WinDef.RECT r = new WinDef.RECT();
                user32.GetWindowRect(hWnd, r);
                label = String.format("Окно %d: (%d, %d)", i + 1, r.left, r.top);
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
                "С какими окнами работаем?",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        return getSelectedWindows(foundWindows, defaultWindowsStr, boxes);
    }

    private static List<HWND> getSelectedWindows(List<HWND> foundWindows,
                                                 String defaultWindowsStr,
                                                 JCheckBox[] boxes) {
        List<HWND> selected = new ArrayList<>();

        for (int i = 0; i < boxes.length; i++) {
            if (boxes[i].isSelected() && foundWindows.get(i) != null) {
                selected.add(foundWindows.get(i));
            }
        }
        if (selected.isEmpty()) {
            return null;
        }

        return selected;
    }

    /* Ищем в Windows все окна с заголовком игры: "Игроклуб" для соцсети МойМир, "2033" для ВКонтакте. Затем
    сортируем список: сначала верх - слева направо, затем низ - слева направо.
    */
    public static List<HWND> findGameWindows() {
        User32 user32 = User32.INSTANCE;
        List<HWND> found = new ArrayList<>();

        user32.EnumWindows((hWnd, data) -> {
            char[] buffer = new char[512];
            user32.GetWindowText(hWnd, buffer, 512);
            String title = new String(buffer).trim();
            if (title.contains("Игроклуб") || title.contains("2033")) {
                found.add(hWnd);
            }
            return true;
        }, null);

        // Сортируем найденные окна по координатам по принципу: сначала верх - слева направо, затем низ - слева направо.
        found.sort((h1, h2) -> {
            WinDef.RECT r1 = new WinDef.RECT();
            WinDef.RECT r2 = new WinDef.RECT();
            user32.GetWindowRect(h1, r1);
            user32.GetWindowRect(h2, r2);
            if (r1.top != r2.top) {
                return Integer.compare(r1.top, r2.top);
            } else {
                return Integer.compare(r1.left, r2.left);
            }
        });

        // Создаём список на 4 окна (возможные позиции)
        List<HWND> ordered = new ArrayList<>(Arrays.asList(null, null, null, null));

        // Определяем разрешение монитора и примерное расположение окон на экране
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        int midX = screen.width / 2;
        int midY = screen.height / 2;

        for (HWND hWnd : found) {
            WinDef.RECT r = new WinDef.RECT();
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

        System.out.println("=== Найдены игровые окна (позиции 1–4) ===");
        for (int i = 0; i < 4; i++) {
            if (ordered.get(i) != null) {
                WinDef.RECT r = new WinDef.RECT();
                user32.GetWindowRect(ordered.get(i), r);
                System.out.printf("Окно %d: (%d, %d)%n",
                        i + 1, r.left, r.top);
            } else {
                System.out.printf("Окно %d: [не найдено]%n", i + 1);
            }
        }

        return ordered;
    }
}
